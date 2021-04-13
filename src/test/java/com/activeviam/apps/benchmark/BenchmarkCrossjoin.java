/*
 * (C) ActiveViam 2020-2021
 * ALL RIGHTS RESERVED. This material is the CONFIDENTIAL and PROPRIETARY
 * property of ActiveViam. Any unauthorized use,
 * reproduction or transfer of this material is strictly prohibited
 */

package com.activeviam.apps.benchmark;

import com.qfs.store.query.plan.condition.IDictionaryOperand;
import com.qfs.store.query.plan.condition.impl.StaticSetOperand;
import com.qfs.store.query.plan.impl.IndexCompiledOperations;
import com.qfs.store.query.plan.impl.IndexCompiledOperations.RangeOperands;
import gnu.trove.TIntCollection;
import gnu.trove.iterator.TIntIterator;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Benchmarking class for
 * {@link IndexCompiledOperations#forPoints(RangeOperands, Object[], Predicate)}.
 *
 * @author ActiveViam
 */
public class BenchmarkCrossjoin extends ABenchmark {

	static {
		SUB_DIRECTORY_NAME = "crossjoin/";
		FILE_NAME = "tests";

		COLUMNS = new String[] {
				"Inline",
				"Operands",
				"Values",
				"Average Crossjoin Time (in us)"
		};

		BENCH_UNIQUE = false;

		NB_WARMUPS = 10;
		NB_RUNS = 10;
	}

	protected static final boolean DEFAULT_INLINE = false;

	protected static final int[] NUM_OPERANDS = new int[] {
			1,
			2,
			3
	};
	protected static final int DEFAULT_NUM_OPERANDS = 3;

	protected static final int[] NUM_VALUES = new int[] {
			2,
			5,
			10,
			20,
			50,
			100
	};
	protected static final int DEFAULT_NUM_VALUES = 10;

	protected boolean inline;
	protected int numOperands;
	protected int numValues;

	protected IDictionaryOperand[] operands;

	public BenchmarkCrossjoin(boolean inline, int numOperands, int numValues) {
		this.inline = inline;
		this.numOperands = numOperands;
		this.numValues = numValues;

		final String version = inline ? "inline" : "general";
		System.out.println("------");
		System.out.println("Test crossjoin with "
				+ version + " version, "
				+ numOperands + " operands, and "
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
		bench(DEFAULT_INLINE, DEFAULT_NUM_OPERANDS, DEFAULT_NUM_VALUES);
	}

	public static void bench(final boolean inline, final int numOperands, final int numValues) {
		BenchmarkCrossjoin tests = new BenchmarkCrossjoin(inline, numOperands, numValues);

		tests.setup();
		tests.run();
		tests.teardown();

		tests = null;
	}

	@Override
	protected void setup() {
		final Set<Object> values =
				IntStream.rangeClosed(1, numValues).boxed().collect(Collectors.toSet());

		final List<Integer> list =
				IntStream.rangeClosed(1, numValues).boxed().collect(Collectors.toList());
		Collections.reverse(list);
		final TIntSet positions = new TIntHashSet(list);

		this.operands = new StaticSetOperand[numOperands];
		for (int i = 0; i < numOperands; ++i) {
			operands[i] = new StaticSetOperand(values, positions);
		}
	}

	@Override
	protected void warmupRun(final int i) {
		forPoints(inline, operands);
	}

	@Override
	protected void realRuns() {
		final RangeOperands rangeOperands = new RangeOperands(operands);
		final Predicate<int[]> proc = point -> true;

		// Get average crossjoin time
		final double averageTime;

		if (!inline) {
			averageTime = executeAndGetAverageTimeInMicros(
					i -> forPoints(rangeOperands, null, proc));

		} else {
			final int numOperands = operands.length;
			switch (numOperands) {
				case 1:
					averageTime = executeAndGetAverageTimeInMicros(
							i -> forPoints(rangeOperands, null, operands[0], proc));
					break;

				case 2:
					averageTime = executeAndGetAverageTimeInMicros(
							i -> forPoints(rangeOperands, null, operands[0], operands[1], proc));
					break;

				case 3:
					averageTime = executeAndGetAverageTimeInMicros(
							i -> forPoints(rangeOperands, null, operands[0], operands[1], operands[2], proc));
					break;

				default:
					throw new IllegalStateException("Cannot handle more than 3 operands !");
			}
		}

		// Add result to file
		addLine(inline, numOperands, numValues, averageTime);
	}

	@Override
	protected void teardown() {
		System.out.println("--- Clear operands ---");

		this.operands = null;
		runGC();
	}

	// Replication of the forPoints methods

	protected static void forPoints(final boolean inline, final IDictionaryOperand[] operands) {
		final RangeOperands rangeOperands = new RangeOperands(operands);
		final Predicate<int[]> proc = point -> true;

		if (!inline) {
			forPoints(rangeOperands, null, proc);
		}

		final int numOperands = operands.length;
		switch (numOperands) {
			case 1:
				forPoints(rangeOperands, null, operands[0], proc);
				break;

			case 2:
				forPoints(rangeOperands, null, operands[0], operands[1], proc);
				break;

			case 3:
				forPoints(rangeOperands, null, operands[0], operands[1], operands[2], proc);
				break;

			default:
				throw new IllegalStateException("Cannot handle more than 3 operands !");
		}
	}

	/**
	 * Produces the crossjoin of all points from the query.
	 *
	 * @param operands query operands
	 * @param parameters parameters for the operands
	 * @param operand single operand for the only field with multiple values
	 * @param proc procedure consuming the generated points
	 */
	protected static void forPoints(
			final RangeOperands operands,
			final Object[] parameters,
			final IDictionaryOperand operand,
			final Predicate<int[]> proc) {

		final int[] point = operands.getPartialPoint(parameters);

		final TIntCollection positions = operand.getPositions(parameters);
		final TIntIterator it = positions.iterator();
		while (it.hasNext()) {
			operands.setValueOfNthOperandWithMultipleValues(point, 0, it.next());
			if (!proc.test(point)) {
				break;
			}
		}
	}

	/**
	 * Produces the crossjoin of all points from the query.
	 *
	 * @param operands query operands
	 * @param parameters parameters for the operands
	 * @param operand1 first operand with multiple values
	 * @param operand2 second operand with multiple values
	 * @param proc procedure consuming the generated points
	 */
	protected static void forPoints(
			final RangeOperands operands,
			final Object[] parameters,
			final IDictionaryOperand operand1,
			final IDictionaryOperand operand2,
			final Predicate<int[]> proc) {

		final int[] point = operands.getPartialPoint(parameters);

		final TIntCollection positions1 = operand1.getPositions(parameters);
		final TIntCollection positions2 = operand2.getPositions(parameters);
		positions1.forEach(v1 -> {
			operands.setValueOfNthOperandWithMultipleValues(point, 0, v1);
			return positions2.forEach(v2 -> {
				operands.setValueOfNthOperandWithMultipleValues(point, 1, v2);

				return proc.test(point);
			});
		});
	}

	/**
	 * Produces the crossjoin of all points from the query.
	 *
	 * @param operands query operands
	 * @param parameters parameters for the operands
	 * @param operand1 first operand with multiple values
	 * @param operand2 second operand with multiple values
	 * @param operand3 third operand with multiple values
	 * @param proc procedure consuming the generated points
	 */
	protected static void forPoints(
			final RangeOperands operands,
			final Object[] parameters,
			final IDictionaryOperand operand1,
			final IDictionaryOperand operand2,
			final IDictionaryOperand operand3,
			final Predicate<int[]> proc) {

		final int[] point = operands.getPartialPoint(parameters);

		final TIntCollection positions1 = operand1.getPositions(parameters);
		final TIntCollection positions2 = operand2.getPositions(parameters);
		final TIntCollection positions3 = operand3.getPositions(parameters);
		positions1.forEach(v1 -> {
			operands.setValueOfNthOperandWithMultipleValues(point, 0, v1);
			return positions2.forEach(v2 -> {
				operands.setValueOfNthOperandWithMultipleValues(point, 1, v2);
				return positions3.forEach(v3 -> {
					operands.setValueOfNthOperandWithMultipleValues(point, 2, v3);

					return proc.test(point);
				});
			});
		});
	}

	/**
	 * Produces the crossjoin of all points from the query
	 * and executes the procedure on each of them.
	 *
	 * @param operands query operands
	 * @param parameters parameters for the operands
	 * @param proc procedure consuming the generated points
	 */
	protected static void forPoints(
			final RangeOperands operands,
			final Object[] parameters,
			final Predicate<int[]> proc) {

		// Get number of operands with multiple values
		final int numOperands = operands.getNumberOfOperandsWithMultipleValues();

		// Get operands with multiple values
		final IDictionaryOperand[] operandsWithMultipleValues = new IDictionaryOperand[numOperands];
		for (int i = 0; i < numOperands; ++i) {
			operandsWithMultipleValues[i] = operands.getNthOperandWithMultipleValues(i);
		}

		// Get point
		final int[] point = operands.getPartialPoint(parameters);

		// Initialize positions and number of positions for each operand
		final int[][] positions = new int[numOperands][];
		final int[] cardinalities = new int[numOperands];

		for (int i = 0; i < numOperands; ++i) {
			// Get positions of nth operand
			final int[] pos = operandsWithMultipleValues[i].getPositions(parameters).toArray();

			// Check that positions is not empty
			if (pos.length == 0) {
				// No point to fetch : end query
				return;
			}

			// Set positions and its cardinality
			positions[i] = pos;
			cardinalities[i] = pos.length;

			// Initialize point
			operands.setValueOfNthOperandWithMultipleValues(point, i, pos[0]);
		}

		// Initialize current index for each loop to 0
		final int[] indexes = new int[numOperands];

		while (indexes[0] < cardinalities[0]) {
			// Execute the procedure consuming the generated point
			proc.test(point);

			// Increment index of the "deepest" loop and set value of operand
			int index = ++indexes[numOperands - 1];
			if (index < cardinalities[numOperands - 1]) {
				final int value = positions[numOperands - 1][index];
				operands.setValueOfNthOperandWithMultipleValues(point, numOperands - 1, value);

			} else {
				// Initialize current loop to the "more nested one"
				int currentLoop = numOperands - 1;

				// While current loop has reached its maximal index
				while (true) {
					// End looping when all loops have reached their maximal index
					if (currentLoop == 0) {
						break;
					}

					// Reset index of current loop and set value of operand
					indexes[currentLoop] = 0;
					int value = positions[currentLoop][0];
					operands.setValueOfNthOperandWithMultipleValues(point, currentLoop, value);

					// Change current loop to the loop "at higher level"
					--currentLoop;

					// Increment index of current loop and set value of operand
					index = ++indexes[currentLoop];
					if (index < cardinalities[currentLoop]) {
						value = positions[currentLoop][index];
						operands.setValueOfNthOperandWithMultipleValues(point, currentLoop, value);

						// Break the while because current loop has not reached its maximal index
						break;
					}
				}
			}
		}
	}

}
