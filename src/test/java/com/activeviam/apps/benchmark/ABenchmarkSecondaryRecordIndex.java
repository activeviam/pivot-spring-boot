/*
 * (C) ActiveViam 2020
 * ALL RIGHTS RESERVED. This material is the CONFIDENTIAL and PROPRIETARY
 * property of ActiveViam. Any unauthorized use,
 * reproduction or transfer of this material is strictly prohibited
 */

package com.activeviam.apps.benchmark;

import com.activeviam.apps.constants.StoreAndFieldConstants;
import com.qfs.desc.IDatastoreSchemaDescription;
import com.qfs.index.ISecondaryRecordIndex;
import com.qfs.index.ISecondaryRecordIndexVersion;
import com.qfs.index.impl.AColumnImprintsSecondaryRecordIndexVersion;
import com.qfs.index.impl.AMultiVersionColumnImprintsSecondaryRecordIndex;
import com.qfs.index.impl.MultiVersionColumnImprintsSecondaryRecordIndex;
import com.qfs.index.impl.MultiVersionColumnImprintsSecondaryRecordIndexWithoutRLECompression;
import com.qfs.monitoring.statistic.memory.MemoryStatisticConstants;
import com.qfs.store.IDatastore;
import com.qfs.store.IDatastoreSchemaVersion;
import com.qfs.store.IStoreVersion;
import com.qfs.store.NoTransactionException;
import com.qfs.store.transaction.DatastoreTransactionException;
import com.qfs.store.transaction.ITransactionManager;
import com.qfs.util.impl.QfsArrays;
import gnu.trove.set.TIntSet;
import java.lang.management.BufferPoolMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Benchmarking class for {@link ISecondaryRecordIndex secondary indexes}.
 *
 * <p>To be run with VM argument: -Xmx20G
 *
 * @author ActiveViam
 */
public abstract class ABenchmarkSecondaryRecordIndex extends ABenchmark {

	static {
		SUB_DIRECTORY_NAME = "secondaryRecordIndex/";
	}

	public static int STORE_SIZE;

	public static int STORE_PARTITIONING;

	// Variables to print the current run
	protected static int totalRuns;
	protected static int currentRun;

	// Datastore variables
	protected IDatastore          datastore;
	protected ITransactionManager transactionManager;

	// Benchmark variables
	protected String indexName;

	// Currencies
	public static final String[] currencies = new String[] {
			"EUR",
			"USD",
			"JPY",
			"GBP",
			"ETB",
			"NOK",
			"RRB"
	};
	protected static final int nbCurrencies = currencies.length;

	// Index types
	public static final Collection<Class<?>> indexTypes = new ArrayList<>();

	@Override
	protected void setup() {
		System.out.println("--- Datastore setup ---");

		setupDatastore();

		// Get transaction manager
		this.transactionManager = datastore.getTransactionManager();

		System.out.println("--- Start transaction ---");
		try {
			transactionManager.startTransaction();
		} catch (DatastoreTransactionException e) {
			e.printStackTrace();
		}

		addAllDataInTransaction();

		System.out.println("--- Commit transaction ---");
		try {
			transactionManager.commitTransaction();
		} catch (NoTransactionException | DatastoreTransactionException e) {
			e.printStackTrace();
		}

		// Print every trades store secondary record index of first partition
		//printIndex();
	}

	protected abstract void setupDatastore();

	protected abstract void addAllDataInTransaction();

	protected void printIndex() {
		// Print every trades store secondary record index of first partition
		if (indexName == MultiVersionColumnImprintsSecondaryRecordIndex.class.getSimpleName()
				|| indexName == MultiVersionColumnImprintsSecondaryRecordIndexWithoutRLECompression
					.class.getSimpleName()) {

			final ISecondaryRecordIndexVersion[] indexes = datastore.getMostRecentVersion()
						.getSchema()
						.getStore(StoreAndFieldConstants.TRADES_STORE_NAME)
						.getPartition(0)
						.getSecondaryIndexes();
			for (final ISecondaryRecordIndexVersion index : indexes) {
				AMultiVersionColumnImprintsSecondaryRecordIndex.printlnIndex(
						(AColumnImprintsSecondaryRecordIndexVersion) index,
						System.out);
			}
		}
	}

	// Method to create datastore schema description

	/**
	 * Provides the schema description of the datastore.
	 *
	 * <p>It is based on the descriptions of the stores in the datastore, the descriptions of the
	 * references between those stores, and the optimizations and constraints set on the schema.
	 *
	 * @return the schema description
	 */
	protected abstract IDatastoreSchemaDescription schemaDescription();

	// Methods to compute memory consumption

	protected Map<String, Long> collectIndexesMemoryStatus() {
		final long[] footprint = new long[2];

		final IDatastoreSchemaVersion schema = datastore.getMostRecentVersion().getSchema();
		final Collection<String> storeNames = schema.getMetadata().getStoreNames();

		for (final String storeName : storeNames) {
			final IStoreVersion storeVersion = schema.getStore(storeName);
			final TIntSet partitionIds = storeVersion.getPartitionIds();
			partitionIds.forEach((partitionId) -> {
				final ISecondaryRecordIndexVersion[] indexes =
						storeVersion.getPartition(partitionId).getSecondaryIndexes();

				for (final ISecondaryRecordIndexVersion index : indexes) {
					footprint[0] += index.getMemoryStatistic().getRetainedOnHeap();
					footprint[1] += index.getMemoryStatistic().getRetainedOffHeap();
				}

				return true;
			});
		}

		return QfsArrays.mutableMap(
				MemoryStatisticConstants.STAT_NAME_GLOBAL_USED_HEAP_MEMORY, footprint[0],
				MemoryStatisticConstants.STAT_NAME_GLOBAL_USED_DIRECT_MEMORY, footprint[1]);
	}

	protected Map<String, Long> collectGlobalMemoryStatus() {
		final MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
		final long usedHeap = memoryBean.getHeapMemoryUsage().getUsed();
		final List<BufferPoolMXBean> beans =
				ManagementFactory.getPlatformMXBeans(BufferPoolMXBean.class);
		BufferPoolMXBean directMemoryBean = null;
		for (BufferPoolMXBean bean : beans) {
			if (bean.getName().equals("direct")) {
				directMemoryBean = bean;
			}
		}
		final long directMemory = directMemoryBean != null ? directMemoryBean.getMemoryUsed() : -1;

		return QfsArrays.mutableMap(
				MemoryStatisticConstants.STAT_NAME_GLOBAL_USED_HEAP_MEMORY, usedHeap,
				MemoryStatisticConstants.STAT_NAME_GLOBAL_USED_DIRECT_MEMORY, directMemory);
	}

}
