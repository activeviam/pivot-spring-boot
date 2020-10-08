/*
 * (C) ActiveViam 2020
 * ALL RIGHTS RESERVED. This material is the CONFIDENTIAL and PROPRIETARY
 * property of ActiveViam. Any unauthorized use,
 * reproduction or transfer of this material is strictly prohibited
 */

package com.activeviam.apps.benchmark;

import com.qfs.condition.impl.BaseConditions;
import com.qfs.store.IDatastore;
import com.qfs.store.IStoreMetadata;
import com.qfs.store.transaction.DatastoreTransactionException;
import com.qfs.store.transaction.ITransactionManager;
import com.quartetfs.fwk.Registry;
import com.quartetfs.fwk.contributions.impl.ClasspathContributionProvider;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.function.IntConsumer;

/**
 * Abstract class for benchmarking.
 *
 * <p>To be run with VM argument: -Xmx20G
 *
 * @author ActiveViam
 */
public abstract class ABenchmark {

	protected static boolean BENCH_UNIQUE;

	public static int NB_WARMUPS;
	public static int NB_RUNS;

	protected static final long BYTES_TO_MEGABYTES = 1_000_000L;

	// Methods managing memory

	protected static final Runtime runtime = Runtime.getRuntime();

	/**
	 * Runs GCs until memory does not reduce anymore.
	 */
	protected static void runGC() {
		System.out.println("--- GC ---");
		long usedMem1 = usedMemory(), usedMem2 = Long.MAX_VALUE;
		for (int i = 0; Math.abs(usedMem1 - usedMem2) > 10_000_000L && i < 500; ++i) {
			runtime.runFinalization();
			runtime.gc();
			usedMem2 = usedMem1;
			usedMem1 = usedMemory();
		}
	}

	/**
	 * Gets the amount of used memory by the JVM.
	 *
	 * @return used memory in bytes
	 */
	protected static long usedMemory() {
		return runtime.totalMemory() - runtime.freeMemory();
	}

	// Benchmark methods

	protected static void init() {
		System.out.println("--- Initialization ---");

		// Set property
		System.setProperty("activeviam.testPhase", "false");

		// Set the registry
		Registry.setContributionProvider(new ClasspathContributionProvider());

		// Initialize file by writing the name of the columns
		initializeFile();
	}

	protected static void end() {
		// Jump a line
		writeAndClose(openAndGetFile(getFullName()), "");

		System.out.println("--- Benchmark ended ---");
	}

	protected abstract void setup();

	protected void run() {
		// Warmup runs
		System.out.println("--- Warmup runs ---");
		for (int i = 0; i < NB_WARMUPS; ++i) {
			warmupRun(i);
		}

		// Run garbage collector
		runGC();

		// Real runs
		System.out.println("--- Real runs ---");
		realRuns();
	}

	protected abstract void warmupRun(int i);

	protected abstract void realRuns();

	protected abstract void teardown();

	protected static double executeAndGetAverageTimeInMicros(final IntConsumer proc) {
		// Get total time in nanos
		final long totalTimeInNanos = executeAndGetTotalTimeInNanos(proc);

		// Get total time in micros
		final double totalTime = TimeUnit.NANOSECONDS.toMicros(totalTimeInNanos);

		// Compute and return average time
		return round(totalTime / NB_RUNS);
	}

	protected static double executeAndGetAverageTimeInMillis(final IntConsumer proc) {
		// Get total time in nanos
		final long totalTimeInNanos = executeAndGetTotalTimeInNanos(proc);

		// Get total time in millis
		final double totalTime = TimeUnit.NANOSECONDS.toMillis(totalTimeInNanos);

		// Compute and return average time
		return round(totalTime / NB_RUNS);
	}

	protected static long executeAndGetTotalTimeInNanos(final IntConsumer proc) {
		// Get start time
		final long preTime = System.nanoTime();

		for (int i = 0; i < NB_RUNS; ++i) {
			proc.accept(i);
		}

		// Get end time
		final long postTime = System.nanoTime();

		// Return total time in nanos
		return postTime - preTime;
	}

	// Methods to empty and stop the datastore

	/**
	 * Empties a datastore from all its data, force the discard of all of its versions, and then
	 * stop it.
	 *
	 * @param datastore the datastore to stop
	 */
	public static void emptyAndStopDatastore(final IDatastore datastore) {
		Throwable prevExc = null;
		try {
			emptyDatastore(datastore);
		} catch (Throwable e) {
			prevExc = e;
		} finally {
			// In any case, lets try to stop the datastore
			try {
				datastore.stop();
			} catch (Throwable e) {
				if (prevExc != null) {
					e.addSuppressed(prevExc);
				}
			}
		}
	}

	/**
	 * Empties a datastore from all its data, and force the discard of all of its versions.
	 *
	 * @param datastore the datastore to stop
	 * @throws DatastoreTransactionException failure
	 */
	public static void emptyDatastore(final IDatastore datastore)
			throws DatastoreTransactionException {

		final ITransactionManager tm = datastore.getTransactionManager();
		long epochId = -1;
		for (final IStoreMetadata sm : datastore.getSchemaMetadata().getStoreGraph().values()) {
			// One transaction per store in case of UW triggers
			tm.startTransaction(sm.getName());
			tm.removeWhere(sm.getName(), BaseConditions.TRUE);
			epochId = tm.commitTransaction().getEpoch().getId();
		}

		// Release and discard all epochs
		for (final String branch : datastore.getEpochManager().getBranches()) {
			datastore.getEpochManager().releaseEpochs(branch, 0, epochId - 1);
		}
		datastore.getEpochManager().forceDiscardEpochs(node -> true);
	}

	// File data

	protected static final String DIRECTORY_NAME = "results/";
	protected static String SUB_DIRECTORY_NAME = "";
	protected static String FILE_NAME = "bench";

	protected static String[] COLUMNS = new String[0];

	protected static String getFullName() {
		return DIRECTORY_NAME + SUB_DIRECTORY_NAME + FILE_NAME + ".data";
	}

	protected static void initializeFile() {
		final FileWriter fw = openAndGetFile(getFullName());

		for (int i = 0; i < COLUMNS.length - 1; ++i) {
			write(fw, COLUMNS[i] + ";");
		}
		writeAndClose(fw, COLUMNS[COLUMNS.length - 1]);
	}

	protected static void addLine(final Object... values) {
		assert values.length == COLUMNS.length;

		final FileWriter fw = openAndGetFile(getFullName());

		for (int i = 0; i < values.length - 1; ++i) {
			write(fw, values[i].toString() + ";");
		}
		writeAndClose(fw, values[values.length - 1].toString());
	}

	// Methods to manipulate file

	protected static FileWriter openAndGetFile(final String fileName) {
		FileWriter fw = null;
		try {
			fw = new FileWriter(fileName, true);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return fw;
	}

	protected static void writeAndGoToNextLine(final FileWriter fw, final String line) {
		write(fw, line);
		goToNextLine(fw);
	}

	protected static void writeAndClose(final FileWriter fw, final String line) {
		writeAndGoToNextLine(fw, line);
		closeFile(fw);
	}

	protected static void write(final FileWriter fw, final String line) {
		try {
			fw.write(line);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected static void goToNextLine(final FileWriter fw) {
		try {
			fw.write(System.lineSeparator());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected static void closeFile(final FileWriter fw) {
		try {
			fw.flush();
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Utility methods

	public static double round(final double value) {
		return Math.round(value * 100D) / 100D;
	}

}
