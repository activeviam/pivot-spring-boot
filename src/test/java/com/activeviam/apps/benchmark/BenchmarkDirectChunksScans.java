/*
 * (C) ActiveViam 2020
 * ALL RIGHTS RESERVED. This material is the CONFIDENTIAL and PROPRIETARY
 * property of ActiveViam. Any unauthorized use,
 * reproduction or transfer of this material is strictly prohibited
 */

package com.activeviam.apps.benchmark;

import com.qfs.bitmap.IBitmap;
import com.qfs.chunk.IChunkLong;
import com.qfs.chunk.IChunkPrimitiveInteger;
import com.qfs.chunk.INativeMemoryAllocator;
import com.qfs.chunk.direct.impl.ADirectChunkPrimitiveInteger;
import com.qfs.chunk.direct.impl.DirectChunkPositiveInteger;
import com.qfs.chunk.impl.Chunks;
import com.qfs.store.query.plan.impl.ResultMerger;
import com.qfs.util.impl.LazyObject;
import com.qfs.util.impl.UnsafeUtil;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;
import java.util.function.Supplier;

/**
 * Benchmarking class for scans in {@link ADirectChunkPrimitiveInteger direct chunks}.
 *
 * <p>To be run with VM argument: -Xmx20G
 *
 * @author ActiveViam
 */
public class BenchmarkDirectChunksScans extends ABenchmarkDirectChunks {

	static {
		SUB_DIRECTORY_NAME += "scans/";
		FILE_NAME = "tests";

		COLUMNS = new String[] {
				"Direct chunk type",
				"Cardinality",
				"Cachelines scanned (in %)",
				"Scan type;Average scan time (in us)"
		};

		BENCH_UNIQUE = false;

		NB_WARMUPS = 10;
		NB_RUNS = 100;

		CHUNK_ORDER = 16;
	}

	public static final int CACHELINE_ORDER = 4;
	public static final int CACHELINE_LENGTH = 1 << CACHELINE_ORDER;

	public static final int NB_CHUNKS = 100;
	public static final int SIZE = NB_CHUNKS << CHUNK_ORDER;

	public static final int cardinality = 100;

	public static final int[] cachelinesScannedInPercents = new int[] {
			1,
			10,
			50,
			100
	};
	public static final int DEFAULT_CACHELINES_SCANNED_IN_PERCENT = 10;

	public static final Integer value = cardinality / 2;

	public static final long       epoch = 1L;
	public static final IChunkLong version;
	static {
		version = Chunks.allocator(false).allocateChunkLong(getChunkSize(), 0L);
		for (int i = 0; i < getChunkSize(); ++i) {
			version.writeLong(i, epoch);
		}
	}

	public enum SCAN_TYPE {
		fullScan("FullScan"),
		cachelines("Cachelines"),
		compressedCachelines("CompressedCachelines");

		private final String name;

		private SCAN_TYPE(String name) {
			this.name = name;
		}

		@Override
		public String toString() {
			return this.name;
		}
	}

	protected IChunkPrimitiveInteger[] chunks;

	protected SCAN_TYPE   type;
	protected int         cachelinesScannedInPercent;

	protected boolean[][] matchingCachelines;
	protected int[][]     compressedMatchingCachelines;

	public BenchmarkDirectChunksScans(int cachelinesScannedInPercent) {
		this.chunks = new IChunkPrimitiveInteger[NB_CHUNKS];
		this.cachelinesScannedInPercent = cachelinesScannedInPercent;
		this.matchingCachelines = new boolean[NB_CHUNKS][getCachelinesPerChunk()];
		this.compressedMatchingCachelines = new int[NB_CHUNKS][];
		this.memoryAllocator = Chunks.allocator().getNativeMemoryAllocator();

		System.out.println("------");
		System.out.println("Test scans with " + NB_CHUNKS + " chunks of size " + getChunkSize()
				+ ", a cardinality of " + cardinality
				+ ", and " + cachelinesScannedInPercent + "% of cachelines scanned per chunk");
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

	public static void benchUnique() {
		// Bench for default value of cachelines scanned in percent
		bench(DEFAULT_CACHELINES_SCANNED_IN_PERCENT);
	}

	public static void benchForEach() {
		// Bench for each number of cachelines scanned
		for (final int cachelinesScannedInPercent : cachelinesScannedInPercents) {
			bench(cachelinesScannedInPercent);
		}
	}

	public static void bench(final int cachelinesScannedInPercent) {
		BenchmarkDirectChunksScans tests = new BenchmarkDirectChunksScans(cachelinesScannedInPercent);

		tests.setup();
		for (final SCAN_TYPE type : SCAN_TYPE.values()) {
			tests.type = type;
			tests.run();
		}
		tests.teardown();
		tests = null;
	}

	@Override
	protected void setup() {
		System.out.println("--- Chunk setup ---");

		final Random rand = new Random(dataGenerationSeed);

		final int order = UnsafeUtil.getOrder(cardinality);
		final int cachelinesScanned = getCachelinesScanned(cachelinesScannedInPercent);

		for (int i = 0; i < NB_CHUNKS; ++i) {
			this.chunks[i] = createDirectChunk(order, memoryAllocator);
			this.matchingCachelines[i] = fillChunk(chunks[i], cachelinesScanned, rand);
			this.compressedMatchingCachelines[i] =
					compressMatchingCachelines(matchingCachelines[i]);
		}
	}

	@Override
	protected void warmupRun(final int i) {
		final LazyObject<IBitmap> result = ResultMerger.createLazyBitmap(SIZE);
		scanChunk(result);
	}

	@Override
	protected void realRuns() {
		// Initialize result bitmap
		final LazyObject<IBitmap> result = ResultMerger.createLazyBitmap(SIZE);

		// Get average scan time
		final double averageTime = executeAndGetAverageTimeInMicros(i -> {
			scanChunk(result);
		});

		// Add result in file
		addLine(
				getDirectChunkName(chunks[0]),
				cardinality,
				cachelinesScannedInPercent,
				type,
				averageTime);
	}

	@Override
	protected void teardown() {
		System.out.println("--- Empty chunk ---");

		this.memoryAllocator = null;
		this.chunks = null;
		runGC();
	}

	protected void scanChunk(final Supplier<IBitmap> result) {
		switch (type) {
		case fullScan:
			for (int chunkId = 0; chunkId < NB_CHUNKS; ++chunkId) {
				chunks[chunkId].findRowsEqualTo(
						value,
						epoch,
						version,
						chunkId << CHUNK_ORDER,
						getChunkSize(),
						null,
						0,
						result.get());
			}
			break;

		case cachelines:
			for (int chunkId = 0; chunkId < NB_CHUNKS; ++chunkId) {
				final IChunkPrimitiveInteger chunk = chunks[chunkId];
				final boolean[] cachelines = matchingCachelines[chunkId];
				final int offset = chunkId << CHUNK_ORDER;

				for (int cacheline = 0; cacheline < getCachelinesPerChunk(); ++cacheline) {
					if (cachelines[cacheline]) {
						chunk.findRowsEqualTo(
								value,
								version,
								epoch,
								offset,
								cacheline << CACHELINE_ORDER,
								CACHELINE_LENGTH,
								null,
								result.get());
					}
				}
			}
			break;

		case compressedCachelines:
			for (int chunkId = 0; chunkId < NB_CHUNKS; ++chunkId) {
				final IChunkPrimitiveInteger chunk = chunks[chunkId];
				final int[] compressedCachelines = compressedMatchingCachelines[chunkId];
				final int offset = chunkId << CHUNK_ORDER;

				boolean current = false;
				int cacheline = 0;
				for (int i = 0; i < compressedCachelines.length; ++i) {
					final int counter = compressedCachelines[i];
					if (current) {
						chunk.findRowsEqualTo(
								value,
								version,
								epoch,
								offset,
								cacheline << CACHELINE_ORDER,
								counter << CACHELINE_ORDER,
								null,
								result.get());
					}
					current = !current;
					cacheline += counter;
				}
			}
			break;

		default:
			assert false: "Scan type does not exist !";
			break;
		}
	}

	public static IChunkPrimitiveInteger createDirectChunk(
			final int order,
			final INativeMemoryAllocator allocator) {

		return DirectChunkPositiveInteger.newChunk(getChunkSize(), order, allocator);
	}

	public static boolean[] fillChunk(
			final IChunkPrimitiveInteger chunk,
			final int cachelinesScanned,
			final Random rand) {

		final boolean[] matchingCachelines = getMatchingCachelines(cachelinesScanned, rand);

		int row = 0;
		for (int i = 0; i < getCachelinesPerChunk(); ++i) {
			if (matchingCachelines[i]) {
				chunk.writeInt(row++, value);
				for (int j = 1; j < CACHELINE_LENGTH; ++j) {
					final int val = rand.nextInt(cardinality);
					chunk.writeInt(row++, val);
				}
			} else {
				for (int j = 0; j < CACHELINE_LENGTH; ++j) {
					int val;
					do {
						val = rand.nextInt(cardinality);
					} while (val == value);
					chunk.writeInt(row++, val);
				}
			}
		}

		return matchingCachelines;
	}

	public static int getCachelinesPerChunk() {
		return 1 << (CHUNK_ORDER - CACHELINE_ORDER);
	}

	public static int getCachelinesScanned(final int cachelinesScannedInPercent) {
		return (cachelinesScannedInPercent << (CHUNK_ORDER - CACHELINE_ORDER)) / 100;
	}

	public static String getDirectChunkName(final IChunkPrimitiveInteger chunk) {
		return chunk.getChunkType();
	}

	public static int[] getKDifferentIntsLesserThanN(final int k, final int n, final Random rand) {
		final int[] N = new int[n];
		for (int i = 0; i < n; ++i) {
			N[i] = i;
		}

		Collections.shuffle(Arrays.asList(N), rand);

		final int[] K = new int[k];
		for (int i = 0; i < k; ++i) {
			K[i] = N[i];
		}
		return K;
	}

	public static boolean[] getMatchingCachelines(final int cachelinesScanned, final Random rand) {
		final int size = getCachelinesPerChunk();
		final int[] cachelines = getKDifferentIntsLesserThanN(cachelinesScanned, size, rand);
		final boolean[] matchingCachelines = new boolean[size];
		for (int i = 0; i < cachelines.length; ++i) {
			matchingCachelines[cachelines[i]] = true;
		}
		return matchingCachelines;
	}

	public static int[] compressMatchingCachelines(final boolean[] matchingCachelines) {
		final int[] result = new int[matchingCachelines.length + 1];

		int size = 0;
		boolean current = false;
		int counter = 0;
		for (int i = 0; i < matchingCachelines.length; ++i) {
			if (matchingCachelines[i] == current) {
				++counter;
			} else {
				result[size++] = counter;
				current = !current;
				counter = 1;
			}
		}
		result[size++] = counter;

		return Arrays.copyOf(result, size);
	}

}
