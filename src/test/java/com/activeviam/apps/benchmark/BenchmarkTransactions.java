/*
 * (C) ActiveViam 2020
 * ALL RIGHTS RESERVED. This material is the CONFIDENTIAL and PROPRIETARY
 * property of ActiveViam. Any unauthorized use,
 * reproduction or transfer of this material is strictly prohibited
 */

package com.activeviam.apps.benchmark;

import static com.qfs.literal.ILiteralType.DOUBLE;
import static com.qfs.literal.ILiteralType.INT;
import static com.qfs.literal.ILiteralType.LOCAL_DATE;
import static com.qfs.literal.ILiteralType.LONG;
import static com.qfs.literal.ILiteralType.STRING;
import com.activeviam.apps.cfg.pivot.CubeConfig;
import com.activeviam.apps.cfg.pivot.PivotManagerConfig;
import com.activeviam.apps.constants.StoreAndFieldConstants;
import com.activeviam.builders.StartBuilding;
import com.activeviam.copper.CopperRegistrations;
import com.qfs.condition.impl.BaseConditions;
import com.qfs.desc.IDatastoreSchemaDescription;
import com.qfs.desc.IReferenceDescription;
import com.qfs.desc.IStoreDescription;
import com.qfs.desc.impl.DatastoreSchemaDescription;
import com.qfs.desc.impl.ReferenceDescription;
import com.qfs.desc.impl.StoreDescriptionBuilder;
import com.qfs.index.impl.IndexManager;
import com.qfs.index.impl.SecondaryIndexPartitionFactoryWithoutDictionary;
import com.qfs.monitoring.statistic.memory.MemoryStatisticConstants;
import com.qfs.multiversion.impl.KeepLastEpochPolicy;
import com.qfs.security.IBranchPermissionsManager;
import com.qfs.security.impl.BranchPermissionsManager;
import com.qfs.store.IMultiVersionSecondaryRecordIndex;
import com.qfs.store.ISecondaryRecordIndex;
import com.qfs.store.impl.MultiVersionColumnImprintsSecondaryRecordIndex;
import com.qfs.store.impl.MultiVersionColumnImprintsSecondaryRecordIndexWithoutRLECompression;
import com.qfs.store.transaction.DatastoreTransactionException;
import com.quartetfs.biz.pivot.IActivePivotManager;
import com.quartetfs.biz.pivot.definitions.IActivePivotInstanceDescription;
import com.quartetfs.biz.pivot.definitions.IActivePivotManagerDescription;
import com.quartetfs.biz.pivot.definitions.impl.ActivePivotDatastorePostProcessor;
import com.quartetfs.biz.pivot.impl.ActivePivotManagerBuilder;
import com.quartetfs.fwk.AgentException;
import com.quartetfs.fwk.IPair;
import com.quartetfs.fwk.impl.Pair;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

/**
 * Benchmarking class for transaction with {@link ISecondaryRecordIndex secondary index}.
 *
 * <p>To be run with VM argument: -Xmx20G
 *
 * @author ActiveViam
 */
public class BenchmarkTransactions extends ABenchmarkSecondaryRecordIndex {

	static {
		SUB_DIRECTORY_NAME += "transactions/";
		FILE_NAME = "tests";

		COLUMNS = new String[] {
				"Scenario",
				"Type",
				"Commit",
				"Timer (in ms)",
				"Heap Memory (in Mb)",
				"Direct Memory (in Mb)",
				"Target Store Size"
		};

		BENCH_UNIQUE = true;

		NB_WARMUPS = 5;
		NB_RUNS = 20; // number of commits

		STORE_SIZE = 20_000_000; // initial store size
		STORE_PARTITIONING = 8;

		//indexTypes.add(MultiVersionLazyDeleteLinkedIndex.class);
		indexTypes.add(MultiVersionColumnImprintsSecondaryRecordIndex.class);
		indexTypes.add(MultiVersionColumnImprintsSecondaryRecordIndexWithoutRLECompression.class);
	}

	public static final int COMMIT_SIZE = 20_000;

	public static int NB_PRODUCTS = 100; // target store size

	// Variables for scenarios
	public enum SCENARIO {
		CommitSomeFactsOnBaseStore("CommitSomeFactsOnBaseStore"),
		CommitSingleFactOnTargetStore("CommitSingleFactOnTargetStore"),
		CommitSingleFactOnIsolatedStore("CommitSingleFactOnIsolatedStore"),
		EmptyThenRecreateTargetStore("EmptyThenRecreateTargetStore"),
		CommitSomeFactsOnBaseStoreFromNonMaster("CommitSomeFactsOnBaseStoreFromNonMaster"),
		CommitSingleFactOnTargetStoreFromNonMaster("CommitSingleFactOnTargetStoreFromNonMaster"),
		UpdateSomeFactsOnBaseStore("UpdateSomeFactsOnBaseStore");

		private final String name;

		private SCENARIO(String name) {
			this.name = name;
		}

		@Override
		public String toString() {
			return this.name;
		}
	}

	public static final SCENARIO[] scenarios = new SCENARIO[] {
			SCENARIO.CommitSomeFactsOnBaseStore,
			SCENARIO.CommitSingleFactOnTargetStore,
			SCENARIO.CommitSingleFactOnIsolatedStore,
			SCENARIO.EmptyThenRecreateTargetStore,
			SCENARIO.CommitSomeFactsOnBaseStoreFromNonMaster,
			SCENARIO.CommitSingleFactOnTargetStoreFromNonMaster,
			SCENARIO.UpdateSomeFactsOnBaseStore
	};
	public static SCENARIO DEFAULT_SCENARIO = SCENARIO.EmptyThenRecreateTargetStore;

	public final SCENARIO scenario;

	// Variables for branches

	/** Property used to limit number of branches in the test, {@code -1} for unlimited branches */
	public static final int MAX_BRANCHES = 5;

	public enum BRANCH_POLICY {
		random("random"),
		master("master"),
		ordered("ordered");

		private final String name;

		private BRANCH_POLICY(String name) {
			this.name = name;
		}

		@Override
		public String toString() {
			return this.name;
		}
	}

	public final Random randomBranchPolicySeed = new Random(0);

	public static final BRANCH_POLICY POLICY = BRANCH_POLICY.ordered;

	protected IActivePivotManager manager;

	/** Constant used to implement the permission for all users to create, read or edit branches */
	protected static final Set<String> ALL_USERS_ALLOWED =
			Collections.unmodifiableSet(new HashSet<>());

	public BenchmarkTransactions(SCENARIO scenario, String indexName) {
		this.scenario = scenario;
		this.indexName = indexName;

		printTitle();
	}

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
		totalRuns = BENCH_UNIQUE ? indexTypes.size() : scenarios.length * indexTypes.size();
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
			// Set index
			final String indexName = indexType.getSimpleName();
			if (indexName.contains("SecondaryRecord")) {
				IndexManager.isSecondaryIndexPartitionFactoryWithoutDictionary = true;
				SecondaryIndexPartitionFactoryWithoutDictionary.setDefaultIndex(
						(Class<? extends IMultiVersionSecondaryRecordIndex>) indexType);
			} else {
				IndexManager.isSecondaryIndexPartitionFactoryWithoutDictionary = false;
			}

			// Start bench
			bench(scenario, indexName);
		}
	}

	public static void bench(final SCENARIO scenario, final String indexName) {
		++currentRun;
		System.out.println("--- Run " + currentRun + "/" + totalRuns + " ---");

		BenchmarkTransactions tests = new BenchmarkTransactions(scenario, indexName);

		tests.setup();
		tests.run();
		tests.teardown();

		tests = null;
	}

	@Override
	protected void setupDatastore() {
		// Create manager description
		final IActivePivotManagerDescription managerDescription = StartBuilding.managerDescription()
				.withSchema()
				.withSelection(PivotManagerConfig
						.createSchemaSelectionDescription(schemaDescription()))
				.withCube(createTestCubeDescription())
				.build();

		// Build datastore
		this.datastore = StartBuilding.datastore()
				.setSchemaDescription(schemaDescription())
				.addSchemaDescriptionPostProcessors(
						ActivePivotDatastorePostProcessor.createFrom(managerDescription))
				.setEpochManagementPolicy(new KeepLastEpochPolicy())
				.build();

		// Create branch permission manager
		final IBranchPermissionsManager branchPermissionsManager = new BranchPermissionsManager(
				ALL_USERS_ALLOWED,
				ALL_USERS_ALLOWED,
				ALL_USERS_ALLOWED);

		// Build a datastore manager
		try {
			this.manager = new ActivePivotManagerBuilder(
					datastore,
					branchPermissionsManager,
					managerDescription)
					.buildAndStart();
		} catch (AgentException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void addAllDataInTransaction() {
		Collection<Object[]> data = generateTradersData();
		transactionManager.addAll(
				transactionManager.getMetadata().getStoreId(
						StoreAndFieldConstants.TRADERS_STORE_NAME),
				data);

		data = generateProductData();
		transactionManager.addAll(
				transactionManager.getMetadata().getStoreId(
						StoreAndFieldConstants.PRODUCT_STORE_NAME),
				data);

		data = generateForexData();
		transactionManager.addAll(
				transactionManager.getMetadata().getStoreId(
						StoreAndFieldConstants.FOREX_STORE_NAME),
				data);

		data = generateTradeData(STORE_SIZE);
		transactionManager.addAll(
				transactionManager.getMetadata().getStoreId(
						StoreAndFieldConstants.TRADES_STORE_NAME),
				data);
	}

	@Override
	protected void warmupRun(final int i) {
		// Warmup runs must be done on master to avoid polluting branches
		final ABenchProcedure warmupProcedure = setupWarmupProcedure(i);
		warmupProcedure.run();
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
					NB_PRODUCTS);

			// Update previous branch
			previousBranch = pair.getLeft();
		}
	}

	@Override
	protected void teardown() {
		System.out.println("--- Empty datastore ---");

		this.transactionManager = null;
		emptyAndStopDatastore(datastore);

		this.datastore = null;
		runGC();
	}

	// Methods to create and setup bench procedures

	protected ABenchProcedure createProcedure(
			final String prevBranch,
			final int id,
			final String branch,
			final int commitSize) {

		switch (scenario) {
		case CommitSomeFactsOnBaseStore:
			return new CommitSomeFactsOnBaseStore(id, commitSize, branch);

		case CommitSingleFactOnTargetStore:
			return new CommitSingleFactOnTargetStore(id, branch);

		case CommitSingleFactOnIsolatedStore:
			return new CommitSingleFactOnIsolatedStore(id, branch);

		case EmptyThenRecreateTargetStore:
			return new EmptyThenRecreateTargetStore(id, branch);

		case CommitSomeFactsOnBaseStoreFromNonMaster:
			return new CommitSomeFactsOnBaseStoreFromNonMaster(id, commitSize, branch, prevBranch);

		case CommitSingleFactOnTargetStoreFromNonMaster:
			return new CommitSingleFactOnTargetStoreFromNonMaster(id, branch, prevBranch);

		case UpdateSomeFactsOnBaseStore:
			return new UpdateSomeFactsOnBaseStore(id, commitSize, branch);

		default:
			throw new RuntimeException("WTF?!");
		}
	}

	@SuppressWarnings("unused")
	protected IPair<String, ABenchProcedure> setupProcedure(final String prevBranch, final int id) {
		final String branch;

		switch (POLICY) {
		case master:
			branch = "master";
			break;

		case ordered:
			branch = (MAX_BRANCHES == -1 ? "b" + id : "b" + (id % MAX_BRANCHES));
			break;

		case random:
			branch = MAX_BRANCHES == -1
					? "b" + Math.abs(randomBranchPolicySeed.nextInt())
					: "b" + (Math.abs(randomBranchPolicySeed.nextInt()) % MAX_BRANCHES);
			break;

		default:
			throw new RuntimeException("WTF?!");
		}

		final ABenchProcedure benchProcedure = createProcedure(prevBranch, id, branch, COMMIT_SIZE);

		System.out.println("[" + id + "] Performing operation on branch " + branch);

		return new Pair<>(branch, benchProcedure);
	}

	protected ABenchProcedure setupWarmupProcedure(final int id) {
		return createProcedure("master", id, "master", 10);
	}

	// Methods to create datastore schema description

	@Override
	protected IDatastoreSchemaDescription schemaDescription() {
		final Collection<IStoreDescription> stores = new LinkedList<>();
		stores.add(createTradesStoreDescription());
		stores.add(createTraderStoreDescription());
		stores.add(createForexStoreDescription());
		stores.add(createProductStoreDescription());
		return new DatastoreSchemaDescription(stores, references());
	}

	protected Collection<IReferenceDescription> references() {
		final Collection<IReferenceDescription> references = new LinkedList<>();
		references.add(createTraderReferenceDescription());
		references.add(createProductsReferenceDescription());
		return references;
	}

	public static IStoreDescription createTradesStoreDescription() {
		return new StoreDescriptionBuilder()
				.withStoreName(StoreAndFieldConstants.TRADES_STORE_NAME)
				.withField(StoreAndFieldConstants.ASOFDATE, LOCAL_DATE).asKeyField()
				.withField(StoreAndFieldConstants.TRADES__TRADEID, LONG).asKeyField()
				.withField(StoreAndFieldConstants.TRADES__TRADER, INT)
				.withField(StoreAndFieldConstants.TRADES__PRODUCT, LONG)
				.withField(StoreAndFieldConstants.TRADES__CURRENCY, STRING)
				.withModuloPartitioning(
						Integer.getInteger("storePartitioning", STORE_PARTITIONING),
						StoreAndFieldConstants.TRADES__TRADEID)
				.build();
	}

	public static IStoreDescription createProductStoreDescription() {
		return new StoreDescriptionBuilder()
				.withStoreName(StoreAndFieldConstants.PRODUCT_STORE_NAME)
				.withField(StoreAndFieldConstants.PRODUCT__PRODUCTID, LONG).asKeyField()
				.withField(StoreAndFieldConstants.PRODUCT__VALUE, DOUBLE)
				.build();
	}

	public static IStoreDescription createTraderStoreDescription() {
		return new StoreDescriptionBuilder()
				.withStoreName(StoreAndFieldConstants.TRADERS_STORE_NAME)
				.withField(StoreAndFieldConstants.TRADERS__TRADERID, INT).asKeyField()
				.withField(StoreAndFieldConstants.TRADERS__NAME, STRING)
				.withField(StoreAndFieldConstants.TRADERS__DESK, STRING)
				.updateOnlyIfDifferent()
				.build();
	}

	public static IStoreDescription createForexStoreDescription() {
		return new StoreDescriptionBuilder()
				.withStoreName(StoreAndFieldConstants.FOREX_STORE_NAME)
				.withField(StoreAndFieldConstants.FOREX_CURRENCY, STRING).asKeyField()
				.withField(StoreAndFieldConstants.FOREX_TARGET_CURRENCY, STRING).asKeyField()
				.withField(StoreAndFieldConstants.FOREX_RATE, DOUBLE)
				.build();
	}

	public static IReferenceDescription createTraderReferenceDescription() {
		return ReferenceDescription.builder()
				.fromStore(StoreAndFieldConstants.TRADES_STORE_NAME)
				.toStore(StoreAndFieldConstants.TRADERS_STORE_NAME)
				.withName(StoreAndFieldConstants.TRADES_TO_TRADER)
				.withMapping(
						StoreAndFieldConstants.TRADES__TRADER,
						StoreAndFieldConstants.TRADERS__TRADERID)
				.build();
	}

	public static IReferenceDescription createProductsReferenceDescription() {
		return ReferenceDescription.builder()
				.fromStore(StoreAndFieldConstants.TRADES_STORE_NAME)
				.toStore(StoreAndFieldConstants.PRODUCT_STORE_NAME)
				.withName(StoreAndFieldConstants.TRADES_TO_PRODUCT)
				.withMapping(
						StoreAndFieldConstants.TRADES__PRODUCT,
						StoreAndFieldConstants.PRODUCT__PRODUCTID)
				.build();
	}

	/**
	 * Builds the test cube instance.
	 *
	 * <p>Let's use the one defined in the main project (i.e. the whole cube).
	 * A smaller test could use a subset of the dimensions if we wished.
	 *
	 * @return the cube instance description
	 */
	public static IActivePivotInstanceDescription createTestCubeDescription() {
		return CubeConfig
				.configureCubeBuilder(StartBuilding.cube(StoreAndFieldConstants.CUBE_NAME))
				.build();
	}

	// Methods to generate data

	protected AtomicLong dataGenerationSeed = new AtomicLong(0);

	protected AtomicLong modificatedTrader = new AtomicLong(0);
	protected AtomicLong tradeIdGenerator =  new AtomicLong(0);

	protected static final Collection<Object[]> traders = new ArrayList<Object[]>(Arrays.asList(
			new Object[] { 1,  "Alice",     "DeskA" },
			new Object[] { 2,  "Bob",       "DeskB" },
			new Object[] { 3,  "Charlotte", "DeskA" },
			new Object[] { 4,  "David",     "DeskB" },
			new Object[] { 5,  "Eliot",     "DeskA" },
			new Object[] { 6,  "Faye",      "DeskB" },
			new Object[] { 7,  "Georges",   "DeskA" },
			new Object[] { 8,  "Henriette", "DeskB" },
			new Object[] { 9,  "Irene",     "DeskA" },
			new Object[] { 10, "John",      "DeskB" },
			new Object[] { 11, "Katharine", "DeskA" },
			new Object[] { 12, "Leon",      "DeskB" },
			new Object[] { 13, "Mylene",    "DeskA" },
			new Object[] { 14, "Nicolas",   "DeskB" },
			new Object[] { 15, "Oprah",     "DeskA" },
			new Object[] { 16, "Patrick",   "DeskB" },
			new Object[] { 17, "Quitterie", "DeskA" },
			new Object[] { 18, "Ray",       "DeskB" },
			new Object[] { 19, "Suzanne",   "DeskA" },
			new Object[] { 20, "Tristan",   "DeskB" }
	));
	protected static final int nbTraders = traders.size();

	protected Collection<Object[]> generateForexData() {
		final Collection<Object[]> res = new ArrayList<Object[]>();
		for (int i = 0; i < nbCurrencies; ++i) {
			for (int j = 0; j < nbCurrencies; ++j) {
				res.add(new Object[] { currencies[i], currencies[j], (i + 1d) / (j + 1d) });
			}
		}
		return res;
	}

	protected Collection<Object[]> generateModifiedForexData() {
		Collection<Object[]> res = new ArrayList<Object[]>();
		for (int i = 0; i < nbCurrencies; ++i) {
			for (int j = 0; j < nbCurrencies; ++j) {
				res.add(new Object[] { currencies[i], currencies[j], (i + 2d) / (j + 2d) });
			}
		}
		return res;
	}

	protected Collection<Object[]> generateTradersData() {
		return traders;
	}

	protected Collection<Object[]> generateModificatedTradersData() {
		Collection<Object[]> res = new ArrayList<Object[]>();
		res.add(new Object[] { 4, "David", "Desk" + modificatedTrader.incrementAndGet() });
		return res;
	}

	protected Collection<Object[]> generateProductData() {
		return LongStream.rangeClosed(1, NB_PRODUCTS)
				.boxed()
				.map(i -> new Object[] { i, i * 10 })
				.collect(Collectors.toList());
	}

	protected Collection<Object[]> generateTradeData(final int size) {
		if (size > 1_000_000) {
			System.out.println("Generating a lot of data, this might take a while");
		}
		final Collection<Object[]> res = new ArrayList<>();
		final Random rand = new Random(dataGenerationSeed.incrementAndGet());
		final LocalDate date = LocalDate.now();
		for (int i = 0; i < size; ++i) {
			if ((i + 1) % 1_000_000 == 0L) {
				System.out.println("Generating data : "
						+ (Math.round((double) (i + 1) / size * 100)) + " % done");
			}
			res.add(new Object[] {
					date, // Date field
					tradeIdGenerator.incrementAndGet(), // TradeID field
					Math.abs(rand.nextInt() % nbTraders) + 1, // Traders field
					Math.abs(rand.nextLong() % NB_PRODUCTS) + 1L, // Products field
					currencies[Math.abs(rand.nextInt() % nbCurrencies)] // Currencies field
			});
		}
		return res;
	}

	protected Collection<Object[]> amendTradeData(final long size, final long maxId) {
		if (size > 1_000_000L) {
			System.out.println("Generating a lot of data, this might take a while");
		}
		final Collection<Object[]> res = new ArrayList<>();
		final Random rand = new Random(dataGenerationSeed.incrementAndGet());
		final LocalDate date = LocalDate.now();
		final int bound = Math.toIntExact(maxId);
		for (long i = 0; i < size; ++i) {
			if ((i + 1) % 1_000_000L == 0L) {
				System.out.println("Generating data : "
						+ (Math.round((double) (i + 1) / size * 100)) + " % done");
			}
			res.add(new Object[] {
					date, // Date field
					Math.abs(rand.nextInt(bound)) + 1L, // TradeID field
					Math.abs(rand.nextInt() % nbTraders) + 1, // Traders field
					Math.abs(rand.nextLong() % NB_PRODUCTS) + 1L, // Products field
					currencies[Math.abs(rand.nextInt() % nbCurrencies)] // Currencies field
			});
		}
		return res;
	}

	// Bench procedures

	public abstract class ABenchProcedure {

		protected long time;
		protected final int id;

		public ABenchProcedure(int id) {
			this.id = id;
		}

		public abstract void run();

		public long getTimerData() {
			return time;
		}

	}

	public class CommitSomeFactsOnBaseStore extends ABenchProcedure {

		public CommitSomeFactsOnBaseStore(int id, int commitSize, String branchName) {
			super(id);
			this.branchName = branchName;
			this.commitSize = commitSize;
		}

		protected final int commitSize;
		protected final String branchName;

		@Override
		public void run() {
			Collection<Object[]> data = generateTradeData(commitSize);

			try {
				final long preTime = System.nanoTime();

				transactionManager.startTransactionOnBranch(branchName, StoreAndFieldConstants.TRADES_STORE_NAME);
				transactionManager.addAll(StoreAndFieldConstants.TRADES_STORE_NAME, data);
				transactionManager.commitTransaction();

				this.time = System.nanoTime() - preTime;

			} catch (IllegalArgumentException | DatastoreTransactionException e) {
				e.printStackTrace();
			}

			data = null;
		}

	}

	public class CommitSomeFactsOnBaseStoreFromNonMaster extends ABenchProcedure {

		public CommitSomeFactsOnBaseStoreFromNonMaster(
				int id,
				int commitSize,
				String branchName,
				String startBranch) {

			super(id);
			this.branchName = branchName;
			this.commitSize = commitSize;
			this.startBranch = startBranch;
		}

		protected final int commitSize;
		protected final String branchName;
		protected final String startBranch;

		@Override
		public void run() {
			Collection<Object[]> data = generateTradeData(commitSize);

			try {
				final long preTime= System.nanoTime();

				if (datastore.getEpochManager().getBranches().contains(branchName)) {
					transactionManager.startTransactionOnBranch(
							branchName,
							StoreAndFieldConstants.TRADES_STORE_NAME);
				} else {
					transactionManager.startTransactionFromBranch(
							branchName,
							startBranch,
							StoreAndFieldConstants.TRADES_STORE_NAME);
				}
				transactionManager.addAll(StoreAndFieldConstants.TRADES_STORE_NAME, data);
				transactionManager.commitTransaction();

				this.time = System.nanoTime() - preTime;

			} catch (IllegalArgumentException | DatastoreTransactionException e) {
				e.printStackTrace();
			}

			data = null;
		}

	}

	public class CommitSingleFactOnTargetStore extends ABenchProcedure {

		public CommitSingleFactOnTargetStore(int id, String branchName) {
			super(id);
			this.branchName = branchName;
		}

		protected final String branchName;

		@Override
		public void run() {
			Collection<Object[]> data = generateModificatedTradersData();

			try {
				final long preTime= System.nanoTime();

				transactionManager.startTransactionOnBranch(branchName, StoreAndFieldConstants.TRADERS_STORE_NAME);
				transactionManager.addAll(StoreAndFieldConstants.TRADERS_STORE_NAME, data);
				transactionManager.commitTransaction();

				this.time = System.nanoTime() - preTime;

			} catch (DatastoreTransactionException e) {
				e.printStackTrace();
			}

			data = null;
		}

	}

	public class CommitSingleFactOnTargetStoreFromNonMaster extends ABenchProcedure {

		public CommitSingleFactOnTargetStoreFromNonMaster(
				int id,
				String branchName,
				String startBranch) {

			super(id);
			this.branchName = branchName;
			this.startBranch = startBranch;
		}

		protected final String branchName;
		protected final String startBranch;

		@Override
		public void run() {
			Collection<Object[]> data = generateModificatedTradersData();

			try {
				final long preTime= System.nanoTime();

				if (datastore.getEpochManager().getBranches().contains(branchName)) {
					transactionManager.startTransactionOnBranch(
							branchName,
							StoreAndFieldConstants.TRADES_STORE_NAME,
							StoreAndFieldConstants.TRADERS_STORE_NAME);
				} else {
					transactionManager.startTransactionFromBranch(
							branchName,
							startBranch,
							StoreAndFieldConstants.TRADES_STORE_NAME,
							StoreAndFieldConstants.TRADERS_STORE_NAME);
				}
				transactionManager.addAll(StoreAndFieldConstants.TRADERS_STORE_NAME, data);
				transactionManager.commitTransaction();

				this.time = System.nanoTime() - preTime;

			} catch (DatastoreTransactionException e) {
				e.printStackTrace();
			}

			data = null;
		}

	}

	public class EmptyThenRecreateTargetStore extends ABenchProcedure {

		public EmptyThenRecreateTargetStore(int id, String branchName) {
			super(id);
			this.branchName = branchName;
		}

		protected final String branchName;

		@Override
		public void run() {
			Collection<Object[]> data = generateProductData();

			try {
				final long preTime = System.nanoTime();

				transactionManager.startTransactionOnBranch(branchName, StoreAndFieldConstants.PRODUCT_STORE_NAME);
				transactionManager.removeWhere(StoreAndFieldConstants.PRODUCT_STORE_NAME, BaseConditions.True());
				transactionManager.addAll(StoreAndFieldConstants.PRODUCT_STORE_NAME, data);
				transactionManager.commitTransaction();

				this.time = System.nanoTime() - preTime;

			} catch (DatastoreTransactionException e) {
				e.printStackTrace();
			}

			data = null;
		}

	}

	public class CommitSingleFactOnIsolatedStore extends ABenchProcedure {

		public CommitSingleFactOnIsolatedStore(int id, String branchName) {
			super(id);
			this.branchName = branchName;
		}

		protected final String branchName;

		@Override
		public void run() {
			Collection<Object[]> data = generateModifiedForexData();

			try {
				final long preTime= System.nanoTime();

				transactionManager.startTransactionOnBranch(branchName, StoreAndFieldConstants.FOREX_STORE_NAME);
				transactionManager.addAll(StoreAndFieldConstants.FOREX_STORE_NAME, data);
				transactionManager.commitTransaction();

				this.time = System.nanoTime() - preTime;

			} catch (IllegalArgumentException | DatastoreTransactionException e) {
				e.printStackTrace();
			}

			data = null;
		}

	}

	public class UpdateSomeFactsOnBaseStore extends ABenchProcedure {

		public UpdateSomeFactsOnBaseStore(int id, int commitSize, String branchName) {
			super(id);
			this.branchName = branchName;
			this.commitSize = commitSize;
		}

		protected final int commitSize;
		protected final String branchName;

		@Override
		public void run() {
			final Collection<Object[]> data = amendTradeData(commitSize, tradeIdGenerator.get());

			try {
				final long preTime= System.nanoTime();

				transactionManager.startTransactionOnBranch(branchName, StoreAndFieldConstants.TRADES_STORE_NAME);
				transactionManager.addAll(StoreAndFieldConstants.TRADES_STORE_NAME, data);
				transactionManager.commitTransaction();

				this.time = System.nanoTime() - preTime;

			} catch (IllegalArgumentException | DatastoreTransactionException e) {
				e.printStackTrace();
			}
		}

	}

}
