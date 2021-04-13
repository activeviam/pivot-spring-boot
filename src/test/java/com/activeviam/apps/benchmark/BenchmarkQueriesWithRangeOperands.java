/*
 * (C) ActiveViam 2020
 * ALL RIGHTS RESERVED. This material is the CONFIDENTIAL and PROPRIETARY
 * property of ActiveViam. Any unauthorized use,
 * reproduction or transfer of this material is strictly prohibited
 */

package com.activeviam.apps.benchmark;

import static com.qfs.condition.impl.BaseConditions.And;
import static com.qfs.condition.impl.BaseConditions.In;

import com.qfs.condition.ICondition;
import com.qfs.desc.IDatastoreSchemaDescription;
import com.qfs.desc.IStoreDescription;
import com.qfs.desc.IStoreDescriptionBuilder.IKeyed;
import com.qfs.desc.impl.DatastoreSchemaDescriptionBuilder;
import com.qfs.desc.impl.StoreDescriptionBuilder;
import com.qfs.literal.ILiteralType;
import com.qfs.store.IDatastore;
import com.qfs.store.IDatastoreVersion;
import com.qfs.store.build.impl.UnitTestDatastoreBuilder;
import com.qfs.store.query.ICompiledQuery;
import com.qfs.store.query.IDictionaryCursor;
import com.qfs.store.query.condition.impl.RecordQuery;
import com.qfs.store.query.impl.QueryCompiler;
import com.qfs.store.query.impl.QueryRunner;
import com.qfs.store.query.plan.impl.IndexCompiledOperations.RangeOperands;
import com.qfs.store.record.IRecordReader;
import com.qfs.store.transaction.DatastoreTransactionException;
import com.qfs.store.transaction.ITransactionManager;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Benchmarking class for queries with {@link RangeOperands}.
 *
 * @author ActiveViam
 */
public class BenchmarkQueriesWithRangeOperands extends ABenchmark {

	static {
		SUB_DIRECTORY_NAME = "rangeOperands/";
		FILE_NAME = "tests";

		COLUMNS = new String[] {
				"Primary",
				"Operands",
				"Values",
				"Average Query Time (in us)"
		};

		BENCH_UNIQUE = true;

		NB_WARMUPS = 10;
		NB_RUNS = 10;
	}

	// Datastore schema variables
	protected static final String STORE = "Store";

	protected static final String K1 = "KeyField1";
	protected static final String K2 = "KeyField2";
	protected static final String K3 = "KeyField3";
	protected static final String K4 = "KeyField4";
	protected static final String K5 = "KeyField5";
	protected static final String K6 = "KeyField6";
	protected static final String K7 = "KeyField7";
	protected static final String K8 = "KeyField8";
	protected static final String K9 = "KeyField9";

	protected static final String V1 = "ValueField1";
	protected static final String V2 = "ValueField2";
	protected static final String V3 = "ValueField3";
	protected static final String V4 = "ValueField4";
	protected static final String V5 = "ValueField5";
	protected static final String V6 = "ValueField6";
	protected static final String V7 = "ValueField7";
	protected static final String V8 = "ValueField8";
	protected static final String V9 = "ValueField9";

	protected static final String R = "RequestedField";

	protected static final String[] VALUE_FIELDS =
			new String[] { V1, V2, V3, V4, V5, V6, V7, V8, V9 };

	protected static final boolean DEFAULT_PRMARY = false;

	protected static final int[] NUM_OPERANDS = new int[] {
			1,
			2,
			3,
			4,
			5,
			6,
			7,
			8,
			9
	};
	protected static final int DEFAULT_NUM_OPERANDS = 3;

	protected static final int[] NUM_VALUES = new int[] {
			2,
			3,
			4,
			5,
			6
	};
	protected static final int DEFAULT_NUM_VALUES = 3;

	// Datastore variables
	protected IDatastore          datastore;
	protected ITransactionManager transactionManager;
	protected IDatastoreVersion   latestVersion;

	protected boolean primary;
	protected int numOperands;
	protected int numValues;

	protected ICompiledQuery query;

	public BenchmarkQueriesWithRangeOperands(boolean primary, int numOperands, int numValues) {
		this.primary = primary;
		this.numOperands = numOperands;
		this.numValues = numValues;

		final String index = primary ? "primary" : "secondary";
		System.out.println("------");
		System.out.println("Test queries with range operands on "
				+ index + " index with "
				+ numOperands + " operands and "
				+ numValues + " values");
		System.out.println("------");
	}

	public static void main(String[] args) {
		// Initialize benchmark
		init();

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
		// For each number of operands
		for (final int numOperands : NUM_OPERANDS) {
			// For each number of values
			for (final int numValues : NUM_VALUES) {
				bench(true, numOperands, numValues);
				bench(false, numOperands, numValues);
			}
		}
	}

	public static void benchUnique() {
		// Bench with default parameters
		bench(DEFAULT_PRMARY, DEFAULT_NUM_OPERANDS, DEFAULT_NUM_VALUES);
	}

	public static void bench(final boolean primary, final int numOperands, final int numValues) {
		BenchmarkQueriesWithRangeOperands tests = new BenchmarkQueriesWithRangeOperands(
				primary,
				numOperands,
				numValues);

		tests.setup();
		tests.run();
		tests.teardown();

		tests = null;
	}

	@Override
	protected void setup() {
		this.datastore = buildDatastore(numOperands, numOperands);

		this.transactionManager = datastore.getTransactionManager();
		try {
			transactionManager.startTransaction();
		} catch (DatastoreTransactionException e) {
			throw new RuntimeException("Failed to start transaction", e);
		}

		final Object[] values = new Integer[2 * numOperands + 1];
		for (int i = 1; i <= numValues; ++i) {
			Arrays.fill(values, i);
			transactionManager.add(STORE, values);
		}

		try {
			transactionManager.commitTransaction();
		} catch (DatastoreTransactionException e) {
			throw new RuntimeException("Failed to commit transaction", e);
		}

		this.latestVersion = datastore.getMostRecentVersion();

		final ICondition condition = getCondition();
		this.query = compile(condition);
	}

	@SuppressWarnings("unused")
	@Override
	protected void warmupRun(final int i) {
		final List<Integer> result = run(query)
				.map(record -> record.read(R))
				.map(Integer.class::cast)
				.collect(Collectors.toList());
	}

	@SuppressWarnings("unused")
	@Override
	protected void realRuns() {
		// Get average query time
		final double averageTime = executeAndGetAverageTimeInMicros(i -> {
			final List<Integer> result = run(query)
					.map(record -> record.read(R))
					.map(Integer.class::cast)
					.collect(Collectors.toList());
		});

		// Add result to file
		addLine(primary, numOperands, numValues, averageTime);
	}

	@Override
	protected void teardown() {
		System.out.println("--- Empty datastore ---");

		this.transactionManager = null;
		emptyAndStopDatastore(datastore);

		this.datastore = null;
		runGC();
	}

	// Methods to build datastore

	protected static IDatastore buildDatastore(final int numKeyFields, final int numValueFields) {
		return new UnitTestDatastoreBuilder()
				.setSchemaDescription(datastore(numKeyFields, numValueFields))
				.build();
	}

	protected static IDatastoreSchemaDescription datastore(
			final int numKeyFields,
			final int numValueFields) {

		return new DatastoreSchemaDescriptionBuilder()
				.withStore(store(numKeyFields, numValueFields))
				.build();
	}

	protected static IStoreDescription store(final int numKeyFields, final int numValueFields) {
		assert numKeyFields > 0 && numKeyFields <= 9;
		assert numValueFields >= 0 && numValueFields <= 9;

		IKeyed builder = new StoreDescriptionBuilder()
				.withStoreName(STORE)
				.withField(K1, ILiteralType.INT).asKeyField();

		for (int i = 2; i <= numKeyFields; ++i) {
			builder = builder.withField("KeyField" + i, ILiteralType.INT).asKeyField();
		}

		for (int i = 1; i <= numValueFields; ++i) {
			builder = builder.withField("ValueField" + i, ILiteralType.INT);
		}

		builder = builder.withField(R, ILiteralType.INT);

		if (numValueFields > 0) {
			builder = builder.withIndexOn(Arrays.copyOf(VALUE_FIELDS, numValueFields));
		}

		return builder.build();
	}

	// Methods to create, compile and run a query

	protected ICondition getCondition() {
		final ICondition[] inConditions = new ICondition[numOperands];
		final Integer[] values =
				IntStream.rangeClosed(1, numValues).boxed().toArray(Integer[]::new);

		if (primary) {
			for (int i = 1; i <= numOperands; ++i) {
				inConditions[i - 1] = In("KeyField" + i, values);
			}
		} else {
			for (int i = 1; i <= numOperands; ++i) {
				inConditions[i - 1] = In("ValueField" + i, values);
			}
		}
		return And(inConditions);
	}

	protected ICompiledQuery compile(final ICondition condition) {
		final QueryCompiler compiler =
				new QueryCompiler(latestVersion.getSchema().getQueryMetadata());
		return new RecordQuery(STORE, condition, R).accept(compiler);
	}

	protected Stream<IRecordReader> run(final ICompiledQuery query) {
		final IDictionaryCursor result = new QueryRunner(latestVersion.getSchema())
				.forQuery(query)
				.withoutParameters()
				.onCurrentThread()
				.run();

		return StreamSupport.stream(result.spliterator(), false);
	}

}
