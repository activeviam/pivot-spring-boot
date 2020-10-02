/*
 * (C) ActiveViam 2020
 * ALL RIGHTS RESERVED. This material is the CONFIDENTIAL and PROPRIETARY
 * property of ActiveViam. Any unauthorized use,
 * reproduction or transfer of this material is strictly prohibited
 */

package com.activeviam.apps.benchmark;

import com.qfs.chunk.INativeMemoryAllocator;
import com.qfs.chunk.direct.impl.ADirectChunkPrimitiveInteger;
import com.qfs.chunk.direct.impl.DirectChunkBits;
import com.qfs.chunk.direct.impl.DirectChunkBytes;
import com.qfs.chunk.direct.impl.DirectChunkHexa;
import com.qfs.chunk.direct.impl.DirectChunkInteger;
import com.qfs.chunk.direct.impl.DirectChunkQuad;
import com.qfs.chunk.direct.impl.DirectChunkShorts;
import com.qfs.chunk.direct.impl.DirectChunkTriBytes;
import com.qfs.chunk.impl.Chunks;
import java.util.Random;

/**
 * Benchmarking class for reads in {@link ADirectChunkPrimitiveInteger direct chunks}.
 *
 * <p>To be run with VM argument: -Xmx20G
 *
 * @author ActiveViam
 */
public class BenchmarkDirectChunksReads extends ABenchmarkDirectChunks {

	static {
		SUB_DIRECTORY_NAME += "reads/";
		FILE_NAME = "tests";

		COLUMNS = new String[] {
				"Direct chunk type",
				"Chunk size",
				"Average read time (in us)"
		};

		BENCH_UNIQUE = false;

		NB_WARMUPS = 10;
		NB_RUNS = 100;

		CHUNK_ORDER = 20;
	}

	public enum DIRECT_CHUNK_TYPE {
		Bits("DirectChunkBits"),
		Quad("DirectChunkQuad"),
		Hexa("DirectChunkHexa"),
		Bytes("DirectChunkBytes"),
		Shorts("DirectChunkShorts"),
		TriBytes("DirectChunkTriBytes"),
		Integer("DirectChunkInteger");

		private final String name;

		private DIRECT_CHUNK_TYPE(String name) {
			this.name = name;
		}

		@Override
		public String toString() {
			return this.name;
		}
	}

	public static final DIRECT_CHUNK_TYPE[] directChunkTypes = new DIRECT_CHUNK_TYPE[] {
			DIRECT_CHUNK_TYPE.Bits,
			DIRECT_CHUNK_TYPE.Quad,
			DIRECT_CHUNK_TYPE.Hexa,
			DIRECT_CHUNK_TYPE.Bytes,
			DIRECT_CHUNK_TYPE.Shorts,
			DIRECT_CHUNK_TYPE.TriBytes,
			DIRECT_CHUNK_TYPE.Integer
	};
	public static final DIRECT_CHUNK_TYPE DEFAULT_DIRECT_CHUNK_TYPE = DIRECT_CHUNK_TYPE.Integer;

	protected ADirectChunkPrimitiveInteger chunk;

	protected DIRECT_CHUNK_TYPE type;

	public BenchmarkDirectChunksReads(DIRECT_CHUNK_TYPE type) {
		this.memoryAllocator = Chunks.allocator().getNativeMemoryAllocator();
		this.type = type;

		System.out.println("------");
		System.out.println("Test " + this.type + " reads with a chunk size of " + getChunkSize());
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
		// Bench for default type
		bench(DEFAULT_DIRECT_CHUNK_TYPE);
	}

	public static void benchForEach() {
		// Bench for each direct chunk type
		for (final DIRECT_CHUNK_TYPE type : directChunkTypes) {
			bench(type);
		}
	}

	public static void bench(final DIRECT_CHUNK_TYPE type) {
		BenchmarkDirectChunksReads tests = new BenchmarkDirectChunksReads(type);

		tests.setup();
		tests.run();
		tests.teardown();
		tests = null;
	}

	@Override
	protected void setup() {
		System.out.println("--- Chunk setup ---");

		this.chunk = createDirectChunk(type, memoryAllocator);

		final Random rand = new Random(dataGenerationSeed);
		int maxValue = chunk.getBoundary() + 1;
		if (maxValue <= 0) {
			maxValue = chunk.getBoundary();
		}

		for (int i = 0; i < chunk.capacity(); ++i) {
			final int value = rand.nextInt(maxValue);
			chunk.writeInt(i, value);
		}
	}

	@SuppressWarnings("unused")
	@Override
	protected void warmupRun(final int i) {
		final int result = readFullChunk();
	}

	@SuppressWarnings("unused")
	@Override
	protected void realRuns() {
		// Get average read time
		final double averageTime = executeAndGetAverageTimeInMicros(i -> {
			final int result = readFullChunk();
		});

		// Add result in file
		addLine(type, getChunkSize(), averageTime);
	}

	@Override
	protected void teardown() {
		System.out.println("--- Empty chunk ---");

		this.memoryAllocator = null;
		this.chunk = null;
		runGC();
	}

	protected int readFullChunk() {
		int result = 0;
		for (int i = 0; i < chunk.capacity(); ++i) {
			result = chunk.readInt(i);
		}
		return result;
	}

	public static ADirectChunkPrimitiveInteger createDirectChunk(
			final DIRECT_CHUNK_TYPE type,
			final INativeMemoryAllocator allocator) {

		switch (type) {
		case Bits:
			return new DirectChunkBits(getChunkSize(), allocator);

		case Quad:
			return new DirectChunkQuad(getChunkSize(), allocator);

		case Hexa:
			return new DirectChunkHexa(getChunkSize(), allocator);

		case Bytes:
			return new DirectChunkBytes(getChunkSize(), allocator);

		case Shorts:
			return new DirectChunkShorts(getChunkSize(), allocator);

		case TriBytes:
			return new DirectChunkTriBytes(getChunkSize(), allocator);

		case Integer:
			return new DirectChunkInteger(getChunkSize(), allocator);

		default:
			assert false : "Unknown type is used";
			return null;
		}
	}

}
