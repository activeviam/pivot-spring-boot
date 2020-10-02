/*
 * (C) ActiveViam 2020
 * ALL RIGHTS RESERVED. This material is the CONFIDENTIAL and PROPRIETARY
 * property of ActiveViam. Any unauthorized use,
 * reproduction or transfer of this material is strictly prohibited
 */

package com.activeviam.apps.benchmark;

import static com.qfs.literal.ILiteralType.INT;
import static com.qfs.literal.ILiteralType.LOCAL_DATE;
import static com.qfs.literal.ILiteralType.STRING;
import com.activeviam.apps.constants.StoreAndFieldConstants;
import com.activeviam.builders.StartBuilding;
import com.qfs.condition.ICondition;
import com.qfs.condition.impl.BaseConditions;
import com.qfs.desc.IDatastoreSchemaDescription;
import com.qfs.desc.IStoreDescription;
import com.qfs.desc.impl.DatastoreSchemaDescription;
import com.qfs.desc.impl.StoreDescriptionBuilder;
import com.qfs.index.impl.IndexManager;
import com.qfs.index.impl.SecondaryIndexPartitionFactoryWithoutDictionary;
import com.qfs.multiversion.impl.KeepLastEpochPolicy;
import com.qfs.store.IMultiVersionSecondaryRecordIndex;
import com.qfs.store.ISecondaryRecordIndex;
import com.qfs.store.impl.MultiVersionColumnImprintsSecondaryRecordIndex;
import com.qfs.store.impl.MultiVersionColumnImprintsSecondaryRecordIndexWithoutRLECompression;
import com.qfs.store.query.ICursor;
import com.qfs.store.query.IRecordQuery;
import com.qfs.store.query.condition.impl.RecordQuery;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Benchmarking class for queries with {@link ISecondaryRecordIndex secondary index}.
 *
 * <p>To be run with VM argument: -Xmx20G
 *
 * @author ActiveViam
 */
public class BenchmarkQueries extends ABenchmarkSecondaryRecordIndex {

	static {
		SUB_DIRECTORY_NAME += "queries/";
		FILE_NAME = "tests";

		COLUMNS = new String[] {
				"Index",
				"Distribution",
				"Cardinality",
				"Indexed fields",
				"Average query time (in ms)"
		};

		BENCH_UNIQUE = true;

		NB_WARMUPS = 10;
		NB_RUNS = 100;

		STORE_SIZE = 20_000_000;
		STORE_PARTITIONING = 1;

		//indexTypes.add(null);
		//indexTypes.add(MultiVersionLazyDeleteLinkedIndex.class);
		indexTypes.add(MultiVersionColumnImprintsSecondaryRecordIndex.class);
		indexTypes.add(MultiVersionColumnImprintsSecondaryRecordIndexWithoutRLECompression.class);
	}

	// Variables to generate data
	public static final double LAMBDA = 8D;
	public static final double LAMBDA_FACTOR = (1D - Math.exp(-LAMBDA));

	public static final int    MEDIAN_GROUP_SIZE = 16;
	public static final int    LEFT_STANDARD_DEVIATION_GROUP_SIZE = 4;
	public static final int    RIGHT_STANDARD_DEVIATION_GROUP_SIZE = 12;

	public static final int    DEFAULT_INDEXED_FIELDS = 1;

	public static final int    NB_TRADERS = 2000;

	public static final int[]  cardinalities = new int[] {
			//10,
			100,
			1_000,
			//10_000,
			//100_000,
			1_000_000
	};
	public static int DEFAULT_CARDINALITY = 1000;

	public enum DISTRIBUTION {
		random("Random"),
		linearIncreasing("LinearIncreasing"),
		nonUniformLinearIncreasing("NonUniformLinearIncreasing"),
		randomGrouped("RandomGrouped"),
		modulo("Modulo");

		private final String name;

		private DISTRIBUTION(String name) {
			this.name = name;
		}

		@Override
		public String toString() {
			return this.name;
		}
	}

	public static final DISTRIBUTION[] distributions = new DISTRIBUTION[] {
			DISTRIBUTION.random,
			//DISTRIBUTION.linearIncreasing,
			//DISTRIBUTION.nonUniformLinearIncreasing,
			DISTRIBUTION.randomGrouped,
			DISTRIBUTION.modulo
	};
	public static DISTRIBUTION DEFAULT_DISTRIBUTION = DISTRIBUTION.modulo;

	protected int          cardinality;
	protected DISTRIBUTION distribution;
	protected int          indexedFields;
	protected boolean      isIndex;

	public BenchmarkQueries(
			DISTRIBUTION distribution,
			int cardinality,
			int indexedFields,
			boolean isIndex,
			String indexName) {

		if (indexedFields <= 0 || indexedFields > 3) {
			throw new IllegalArgumentException(
					"There must be 1, 2, or 3 indexed fields, not " + indexedFields);
		}

		this.distribution = distribution;
		this.cardinality = cardinality;
		this.indexedFields = indexedFields;
		this.isIndex = isIndex;
		this.indexName = indexName;

		printTitle();
	}

	protected void printTitle() {
		System.out.println("------");
		System.out.println("Test queries with " + indexName
				+ " (distribution = " + distribution
				+ ", cardinality = " + cardinality
				+ ", indexed fields = " + indexedFields + ")");
		System.out.println("------");
	}

	public static void main(String[] args) {
		// Initialize benchmark
		init();

		// Initialize run counter
		totalRuns = BENCH_UNIQUE
				? indexTypes.size()
				: cardinalities.length * distributions.length * indexTypes.size();
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
			// Set index
			final boolean isIndex = (null != indexType);
			String indexName = "FullScan";
			if (isIndex) {
				indexName = indexType.getSimpleName();
				if (indexName.contains("SecondaryRecord")) {
					IndexManager.isSecondaryIndexPartitionFactoryWithoutDictionary = true;
					SecondaryIndexPartitionFactoryWithoutDictionary.setDefaultIndex(
							(Class<? extends IMultiVersionSecondaryRecordIndex>) indexType);
				} else {
					IndexManager.isSecondaryIndexPartitionFactoryWithoutDictionary = false;
				}
			}

			// Start bench
			bench(distribution, cardinality, isIndex, indexName);
		}
	}

	public static void bench(
			final DISTRIBUTION distribution,
			final int cardinality,
			final boolean isIndex,
			final String indexName) {

		++currentRun;
		System.out.println("--- Run " + currentRun + "/" + totalRuns + " ---");

		BenchmarkQueries tests = new BenchmarkQueries(
				distribution,
				cardinality,
				DEFAULT_INDEXED_FIELDS,
				isIndex,
				indexName);

		tests.setup();
		tests.run();
		tests.teardown();

		tests = null;
	}

	@Override
	protected void setupDatastore() {
		// Build datastore
		this.datastore = StartBuilding.datastore()
				.setSchemaDescription(schemaDescription())
				.setEpochManagementPolicy(new KeepLastEpochPolicy())
				.build();
	}

	@Override
	protected void addAllDataInTransaction() {
		// Initialize store data
		final Collection<Object[]> data = generateTradeData();

		transactionManager.addAll(
				transactionManager
					.getMetadata()
					.getStoreId(StoreAndFieldConstants.TRADES_STORE_NAME),
				data);
	}

	@Override
	protected void warmupRun(final int i) {
		final int[] values = new int[] {
				i % cardinality,
				i % NB_TRADERS,
				i % currencies.length
		};
		final IRecordQuery query = createQuery(values, indexedFields);
		executeQuery(query);
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
				averageTime);
	}

	@Override
	protected void teardown() {
		System.out.println("--- Empty datastore ---");

		this.transactionManager = null;
		emptyAndStopDatastore(datastore);

		this.datastore = null;
		runGC();
	}

	// Methods to create and execute queries

	/**
	 * Executes a query on the latest version of the store.
	 *
	 * @param query the query
	 */
	protected void executeQuery(final IRecordQuery query) {
		final ICursor result = datastore.getMostRecentVersion().execute(query);
		while (result.hasNext()) {
			result.next();
			result.getRecord().read(0);
		}
	}

	/**
	 * Creates a query matching the given values.
	 *
	 * @param values the values
	 * @param indexedFields the number of indexed fields
	 * @return the query
	 */
	public static IRecordQuery createQuery(final int[] values, final int indexedFields) {
		// Create condition
		final List<ICondition> conditions = new ArrayList<>(3);
		conditions.add(BaseConditions.Equal(StoreAndFieldConstants.TRADES__PRODUCT, values[0]));
		if (indexedFields >= 2) {
			conditions.add(BaseConditions.Equal(StoreAndFieldConstants.TRADES__TRADER, values[1]));
			if (indexedFields == 3) {
				conditions.add(BaseConditions.Equal(
						StoreAndFieldConstants.TRADES__CURRENCY,
						currencies[values[2]]));
			}
		}
		final ICondition condition = BaseConditions.And(conditions);

		// Create query
		return new RecordQuery(
				StoreAndFieldConstants.TRADES_STORE_NAME,
				condition,
				StoreAndFieldConstants.TRADES__TRADEID);
	}

	// Method to create datastore schema description

	@Override
	protected IDatastoreSchemaDescription schemaDescription() {
		final Collection<IStoreDescription> stores = new LinkedList<>();
		stores.add(createTradesStoreDescription(isIndex, indexedFields));
		return new DatastoreSchemaDescription(stores, new LinkedList<>());
	}

	public static IStoreDescription createTradesStoreDescription(
			final boolean isIndex,
			final int indexedFields) {

		if (isIndex) {
			switch (indexedFields) {
			case 1:
				return new StoreDescriptionBuilder()
						.withStoreName(StoreAndFieldConstants.TRADES_STORE_NAME)
						.withField(StoreAndFieldConstants.ASOFDATE, LOCAL_DATE).asKeyField()
						.withField(StoreAndFieldConstants.TRADES__TRADEID, INT).asKeyField()
						.withField(StoreAndFieldConstants.TRADES__TRADER, INT)
						.withField(StoreAndFieldConstants.TRADES__PRODUCT, INT)
						.withField(StoreAndFieldConstants.TRADES__CURRENCY, STRING)
						.withModuloPartitioning(
								StoreAndFieldConstants.TRADES__TRADEID,
								STORE_PARTITIONING)
						.withIndexOn(StoreAndFieldConstants.TRADES__PRODUCT)
						.build();

			case 2:
				return new StoreDescriptionBuilder()
						.withStoreName(StoreAndFieldConstants.TRADES_STORE_NAME)
						.withField(StoreAndFieldConstants.ASOFDATE, LOCAL_DATE).asKeyField()
						.withField(StoreAndFieldConstants.TRADES__TRADEID, INT).asKeyField()
						.withField(StoreAndFieldConstants.TRADES__TRADER, INT)
						.withField(StoreAndFieldConstants.TRADES__PRODUCT, INT)
						.withField(StoreAndFieldConstants.TRADES__CURRENCY, STRING)
						.withModuloPartitioning(
								StoreAndFieldConstants.TRADES__TRADEID,
								STORE_PARTITIONING)
						.withIndexOn(
								StoreAndFieldConstants.TRADES__TRADER,
								StoreAndFieldConstants.TRADES__PRODUCT)
						.build();

			case 3:
				return new StoreDescriptionBuilder()
						.withStoreName(StoreAndFieldConstants.TRADES_STORE_NAME)
						.withField(StoreAndFieldConstants.ASOFDATE, LOCAL_DATE).asKeyField()
						.withField(StoreAndFieldConstants.TRADES__TRADEID, INT).asKeyField()
						.withField(StoreAndFieldConstants.TRADES__TRADER, INT)
						.withField(StoreAndFieldConstants.TRADES__PRODUCT, INT)
						.withField(StoreAndFieldConstants.TRADES__CURRENCY, STRING)
						.withModuloPartitioning(
								StoreAndFieldConstants.TRADES__TRADEID,
								STORE_PARTITIONING)
						.withIndexOn(
								StoreAndFieldConstants.TRADES__TRADER,
								StoreAndFieldConstants.TRADES__PRODUCT,
								StoreAndFieldConstants.TRADES__CURRENCY)
						.build();

			default:
				throw new IllegalArgumentException(
						"There must be 1, 2, or 3 indexed fields, not " + indexedFields);
			}
		}

		return new StoreDescriptionBuilder()
				.withStoreName(StoreAndFieldConstants.TRADES_STORE_NAME)
				.withField(StoreAndFieldConstants.ASOFDATE, LOCAL_DATE).asKeyField()
				.withField(StoreAndFieldConstants.TRADES__TRADEID, INT).asKeyField()
				.withField(StoreAndFieldConstants.TRADES__TRADER, INT)
				.withField(StoreAndFieldConstants.TRADES__PRODUCT, INT)
				.withField(StoreAndFieldConstants.TRADES__CURRENCY, STRING)
				.withModuloPartitioning(StoreAndFieldConstants.TRADES__TRADEID, STORE_PARTITIONING)
				.build();
	}

	// Method to generate data

	protected static final long dataGenerationSeed = 42L;
	protected static final long queryGenerationSeed = 4895L;

	protected Collection<Object[]> generateTradeData() {
		final Collection<Object[]> data = new ArrayList<Object[]>();
		final Random rand = new Random(dataGenerationSeed);

		if (STORE_SIZE > 1_000_000) {
			System.out.println("Generating a lot of data, this might take a while");
		}

		int product = 0, trader = 0, currency = 0;

		// Initialize variables for increasing distributions
		final double increasingProductSpeed = (double) cardinality / STORE_SIZE;
		int maxProduct = 0, newMaxProduct = 0;

		// Initialize variable for grouped distributions
		int groupSize = 0;

		for (int row = 0; row < STORE_SIZE; ++row) {
			if ((row + 1) % 1_000_000 == 0) {
				System.out.println("Generating data : "
						+ Math.round((double) (row + 1) / STORE_SIZE * 100) + " % done");
			}

			switch (distribution) {
			// Random distribution
			case random:
				product = rand.nextInt(cardinality);
				break;

			// Linear increasing distribution
			case linearIncreasing:
				newMaxProduct = (int) (row * increasingProductSpeed);
				if (newMaxProduct > maxProduct) {
					maxProduct = newMaxProduct;
					product = maxProduct;
				} else {
					product = rand.nextInt(maxProduct + 1);
				}
				break;

			// Non-uniform linear increasing distribution
			case nonUniformLinearIncreasing:
				newMaxProduct = (int) (row * increasingProductSpeed);
				if (newMaxProduct > maxProduct) {
					maxProduct = newMaxProduct;
					product = maxProduct;
				} else {
					product = nextExponential(maxProduct + 1, rand);
				}
				break;

			// Random grouped distribution
			case randomGrouped:
				if (groupSize <= 0) {
					groupSize = nextGroupSize(rand);
					product = rand.nextInt(cardinality);
				}
				--groupSize;
				break;

			// Modulo distribution
			case modulo:
				product = row % cardinality;
				break;

			default:
				assert false : "Unknown distribution is used";
			}

			trader = rand.nextInt(NB_TRADERS);
			currency = rand.nextInt(currencies.length);

			data.add(new Object[] {
					LocalDate.now(), // Date field
					row, // TradeID field
					trader, // Trader field
					product, // Product field
					currencies[currency] // Currency field
			});
		}

		return data;
	}

	/**
	 * Returns a random integer between 0 (inclusive) and max (exclusive) following
	 * a pseudo exponential distribution of parameter {@link BenchmarkQueries#LAMBDA lambda}.
	 *
	 * @param max the maximum value
	 * @param rnd the random number generator
	 * @return the generated random integer
	 */
	public static int nextExponential(final int max, final Random rnd) {
		final double result = max * (1D - Math.exp(-LAMBDA * rnd.nextDouble())) / LAMBDA_FACTOR;
		return (int) result;
	}

	/**
	 * Returns a random strictly positive integer representing a group size.
	 *
	 * @param rnd the random number generator
	 * @return the generated random integer
	 */
	public static int nextGroupSize(final Random rnd) {
		double result = rnd.nextGaussian();
		if (result < 0) {
			result *= LEFT_STANDARD_DEVIATION_GROUP_SIZE;
		} else {
			result *= RIGHT_STANDARD_DEVIATION_GROUP_SIZE;
		}
		result += MEDIAN_GROUP_SIZE;
		int groupSize = (int) Math.round(result);
		if (groupSize < 1) {
			groupSize = 1;
		}
		return groupSize;
	}

}
