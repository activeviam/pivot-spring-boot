/*
 * (C) ActiveViam 2020
 * ALL RIGHTS RESERVED. This material is the CONFIDENTIAL and PROPRIETARY
 * property of ActiveViam. Any unauthorized use,
 * reproduction or transfer of this material is strictly prohibited
 */

package com.activeviam.apps.benchmark;

import com.activeviam.copper.CopperRegistrations;
import com.qfs.index.impl.IndexManager;
import com.qfs.index.impl.SecondaryIndexPartitionFactoryWithoutDictionary;
import com.qfs.monitoring.statistic.memory.MemoryStatisticConstants;
import com.qfs.store.IMultiVersionSecondaryRecordIndex;
import com.qfs.store.impl.AMultiVersionColumnImprintsSecondaryRecordIndex;
import com.qfs.store.impl.MultiVersionColumnImprintsSecondaryRecordIndex;
import com.qfs.store.impl.MultiVersionColumnImprintsSecondaryRecordIndexWithoutRLECompression;
import com.quartetfs.fwk.IPair;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Benchmarking class for transactions on a datastore with
 * {@link AMultiVersionColumnImprintsSecondaryRecordIndex column imprints secondary index}
 * with different cacheline lengths.
 *
 * <p>To be run with VM argument: -Xmx20G
 *
 * @author ActiveViam
 */
public class BenchmarkTransactionsWithCachelineLength extends BenchmarkTransactions {

	static {
		SUB_DIRECTORY_NAME += "cachelineLength/";
		FILE_NAME = "definitiveBench";

		COLUMNS = new String[] {
				"Scenario",
				"Type",
				"Commit",
				"Timer (in ms)",
				"Heap Memory (in Mb)",
				"Direct Memory (in Mb)",
				"Target Store Size",
				"Cacheline Length"
		};

		BENCH_UNIQUE = false;

		NB_WARMUPS = 5;
		NB_RUNS = 20; // number of commits

		STORE_SIZE = 20_000_000; // initial store size
		STORE_PARTITIONING = 8;

		NB_PRODUCTS = 100; // target store size

		DEFAULT_SCENARIO = SCENARIO.EmptyThenRecreateTargetStore;

		indexTypes.clear();
		indexTypes.add(MultiVersionColumnImprintsSecondaryRecordIndex.class);
		indexTypes.add(MultiVersionColumnImprintsSecondaryRecordIndexWithoutRLECompression.class);
	}

	protected static final int[] cachelineOrders = new int[] {
			3,
			4,
			5,
			6
	};

	protected final int cachelineOrder;

	public BenchmarkTransactionsWithCachelineLength(
			SCENARIO scenario,
			String indexName,
			int cachelineOrder) {

		super(scenario, indexName);

		this.cachelineOrder = cachelineOrder;
	}

	@Override
	protected void printTitle() {
		System.out.println("------");
		System.out.println("Doing scenario " + scenario + " with the " + POLICY + " policy on "
				+ MAX_BRANCHES + " maximum branches, and index " + indexName);
		System.out.println("------");
	}

	public static void main(String[] args) {
		// Initialize benchmark
		init();

		// Initialize run counter
		totalRuns = BENCH_UNIQUE ? 1 : scenarios.length;
		totalRuns *= indexTypes.size() * cachelineOrders.length;
		currentRun = 0;

		// Benchmark
		if (BENCH_UNIQUE) {
			benchUnique();
		} else {
			benchForEach();
		}

		// End benchmark
		CopperRegistrations.REGISTRY_SETUP_WITH_CLASSES.clear();
		end();
	}

	public static void benchForEach() {
		// Bench for each scenario for each index
		for (final SCENARIO scenario : scenarios) {
			benchForEachIndex(scenario);
		}
	}

	public static void benchUnique() {
		// Bench for each index with default scenario
		benchForEachIndex(DEFAULT_SCENARIO);
	}

	@SuppressWarnings("unchecked")
	public static void benchForEachIndex(final SCENARIO scenario) {
		// For each index
		for (final Class<?> indexType : indexTypes) {
			// Get index name
			final String indexName = indexType.getSimpleName();

			// Check index is a column imprints
			if (!indexName.contains("ColumnImprints")) {
				throw new IllegalStateException("The benchmarked index must be a column imprints.");
			}

			// Set index
			IndexManager.isSecondaryIndexPartitionFactoryWithoutDictionary = true;
			SecondaryIndexPartitionFactoryWithoutDictionary.setDefaultIndex(
					(Class<? extends IMultiVersionSecondaryRecordIndex>) indexType);

			// Start bench
			benchForEachCachelineOrder(scenario, indexName);
		}
	}

	public static void benchForEachCachelineOrder(
			final SCENARIO scenario,
			final String indexName) {

		// For each cacheline order
		for (final int cachelineOrder : cachelineOrders) {
			// Set cacheline order
			// Set DEFAULT_CACHELINE_ORDER to public and non-final in
			// AColumnImprintsSecondaryRecordIndexBase in order to set this variable here
			//FIXME AColumnImprintsSecondaryRecordIndexBase.DEFAULT_CACHELINE_ORDER = cachelineOrder;

			// Start bench
			bench(scenario, indexName, cachelineOrder);
		}
	}

	public static void bench(
			final SCENARIO scenario,
			final String indexName,
			final int cachelineOrder) {

		++currentRun;
		System.out.println("--- Run " + currentRun + "/" + totalRuns + " ---");

		BenchmarkTransactionsWithCachelineLength tests =
				new BenchmarkTransactionsWithCachelineLength(scenario, indexName, cachelineOrder);

		tests.setup();
		tests.run();
		tests.teardown();

		tests = null;
	}

	@Override
	protected void realRuns() {
		// Initialize previous branch
		String previousBranch = "master";

		for (int i = 0; i < NB_RUNS; ++i) {
			datastore.getEpochManager().forceDiscardEpochs(__ -> true);

			// GC runs to ensure cleaned memory, be careful it is quite slow
			if (i % 50 == 49) {
				runGC();
			}

			// Create and run bench procedure
			final IPair<String,ABenchProcedure> pair = setupProcedure(previousBranch, i);
			final ABenchProcedure benchProcedure = pair.getRight();
			benchProcedure.run();

			// Get memory status
			final Map<String, Long> memory = collectIndexesMemoryStatus();

			// Add procedure result to file
			addLine(
					scenario,
					indexName,
					i + 1,
					TimeUnit.NANOSECONDS.toMillis(benchProcedure.getTimerData()),
					memory.get(MemoryStatisticConstants.STAT_NAME_GLOBAL_USED_HEAP_MEMORY) / BYTES_TO_MEGABYTES,
					memory.get(MemoryStatisticConstants.STAT_NAME_GLOBAL_USED_DIRECT_MEMORY) / BYTES_TO_MEGABYTES,
					NB_PRODUCTS,
					getCachelineLength());

			// Update previous branch
			previousBranch = pair.getLeft();
		}
	}

	protected int getCachelineLength() {
		return 1 << cachelineOrder;
	}

}
