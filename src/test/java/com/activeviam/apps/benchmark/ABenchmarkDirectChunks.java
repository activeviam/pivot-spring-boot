/*
 * (C) ActiveViam 2020
 * ALL RIGHTS RESERVED. This material is the CONFIDENTIAL and PROPRIETARY
 * property of ActiveViam. Any unauthorized use,
 * reproduction or transfer of this material is strictly prohibited
 */

package com.activeviam.apps.benchmark;

import com.qfs.chunk.INativeMemoryAllocator;
import com.qfs.chunk.direct.impl.ADirectChunkPrimitiveInteger;

/**
 * Benchmarking class for {@link ADirectChunkPrimitiveInteger direct chunks}.
 *
 * <p>To be run with VM argument: -Xmx20G
 *
 * @author ActiveViam
 */
public abstract class ABenchmarkDirectChunks extends ABenchmark {

	static {
		SUB_DIRECTORY_NAME = "directChunks/";
	}

	public static int CHUNK_ORDER;

	protected static int getChunkSize() {
		return 1 << CHUNK_ORDER;
	}

	protected INativeMemoryAllocator memoryAllocator;

	protected static final long dataGenerationSeed = 42L;

}
