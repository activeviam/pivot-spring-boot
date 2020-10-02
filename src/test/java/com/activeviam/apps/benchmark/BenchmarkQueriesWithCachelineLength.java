/*
 * (C) ActiveViam 2020
 * ALL RIGHTS RESERVED. This material is the CONFIDENTIAL and PROPRIETARY
 * property of ActiveViam. Any unauthorized use,
 * reproduction or transfer of this material is strictly prohibited
 */

package com.activeviam.apps.benchmark;

import com.qfs.index.impl.IndexManager;
import com.qfs.index.impl.SecondaryIndexPartitionFactoryWithoutDictionary;
import com.qfs.store.IMultiVersionSecondaryRecordIndex;
import com.qfs.store.impl.AColumnImprintsSecondaryRecordIndexBase;
import com.qfs.store.impl.AMultiVersionColumnImprintsSecondaryRecordIndex;
import com.qfs.store.impl.MultiVersionColumnImprintsSecondaryRecordIndex;
import com.qfs.store.impl.MultiVersionColumnImprintsSecondaryRecordIndexWithoutRLECompression;
import com.qfs.store.query.IRecordQuery;
import java.util.Random;

/**
 * Benchmarking class for {@link AMultiVersionColumnImprintsSecondaryRecordIndex column imprints
 * secondary index} queries with different cacheline lengths.
 *
 * <p>To be run with VM argument: -Xmx20G
 *
 * @author ActiveViam
 */
public class BenchmarkQueriesWithCachelineLength extends BenchmarkQueries {

	static {
		SUB_DIRECTORY_NAME += "cachelineLength/";
		FILE_NAME = "tests";

		COLUMNS = new String[] {
				"Index",
				"Distribution",
				"Cardinality",
				"Indexed fields",
				"Cacheline length",
				"Average query time (in ms)"
		};

		BENCH_UNIQUE = true;

		NB_WARMUPS = 10;
		NB_RUNS = 100;

		STORE_SIZE = 20_000_000;
		STORE_PARTITIONING = 1;

		DEFAULT_CARDINALITY = 1000;
		DEFAULT_DISTRIBUTION = DISTRIBUTION.modulo;

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

	protected int cachelineOrder;

	public BenchmarkQueriesWithCachelineLength(
			DISTRIBUTION distribution,
			int cardinality,
			int indexedFields,
			String indexName,
			int cachelineOrder) {

		super(distribution, cardinality, indexedFields, true, indexName);

		this.cachelineOrder = cachelineOrder;
	}

	@Override
	protected void printTitle() {
		System.out.println("------");
		System.out.println("Test queries with " + indexName
				+ " (distribution = " + distribution
				+ ", cardinality = " + cardinality
				+ ", indexed fields = " + indexedFields
				+ ", cacheline length = " + getCachelineLength() + ")");
		System.out.println("------");
	}

	public static void main(String[] args) {
		// Initialize benchmark
		init();

		// Initialize run counter
		totalRuns = BENCH_UNIQUE ? 1 : cardinalities.length * distributions.length;
		totalRuns *= indexTypes.size() * cachelineOrders.length;
		currentRun = 0;

		// Benchmark
		if (BENCH_UNIQUE) {
			benchUnique();
		} else {
			benchForEach();
		}

		// End benchmark
		end();
	}

	public static void benchForEach() {
		// Bench for each index with all possible parameters

		// For each distribution
		for (final DISTRIBUTION distribution : distributions) {
			// For each cardinality
			for (final int cardinality : cardinalities) {
				benchForEachIndex(distribution, cardinality);
			}
		}
	}

	public static void benchUnique() {
		// Bench for each index with defaults parameters
		benchForEachIndex(DEFAULT_DISTRIBUTION, DEFAULT_CARDINALITY);
	}

	@SuppressWarnings("unchecked")
	public static void benchForEachIndex(final DISTRIBUTION distribution, final int cardinality) {
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

			// Bench for each cacheline order
			benchForEachCachelineOrder(distribution, cardinality, indexName);
		}
	}

	public static void benchForEachCachelineOrder(
			final DISTRIBUTION distribution,
			final int cardinality,
			final String indexName) {

		// For each cacheline order
		for (final int cachelineOrder : cachelineOrders) {
			// Set cacheline order
			AColumnImprintsSecondaryRecordIndexBase.DEFAULT_CACHELINE_ORDER = cachelineOrder;

			// Start bench
			bench(distribution, cardinality, indexName, cachelineOrder);
		}
	}

	public static void bench(
			final DISTRIBUTION distribution,
			final int cardinality,
			final String indexName,
			final int cachelineOrder) {

		++currentRun;
		System.out.println("--- Run " + currentRun + "/" + totalRuns + " ---");

		BenchmarkQueriesWithCachelineLength tests = new BenchmarkQueriesWithCachelineLength(
				distribution,
				cardinality,
				DEFAULT_INDEXED_FIELDS,
				indexName,
				cachelineOrder);

		tests.setup();
		tests.run();
		tests.teardown();

		tests = null;
	}

	@Override
	protected void realRuns() {
		// Initialize random
		final Random rand = new Random(queryGenerationSeed);

		// Generate queries
		final IRecordQuery[] queries = new IRecordQuery[NB_RUNS];
		for (int i = 0; i < NB_RUNS; ++i) {
			final int[] values = new int[] {
					rand.nextInt(cardinality),
					rand.nextInt(NB_TRADERS),
					rand.nextInt(currencies.length)
			};
			queries[i] = createQuery(values, indexedFields);
		}

		// Get average query time
		final double averageTime = executeAndGetAverageTimeInMillis(i -> {
			executeQuery(queries[i]);
		});

		// Add result to file
		addLine(
				indexName,
				distribution,
				cardinality,
				indexedFields,
				getCachelineLength(),
				averageTime);
	}

	protected int getCachelineLength() {
		return 1 << cachelineOrder;
	}

}
