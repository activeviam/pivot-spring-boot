package com.activeviam.apps.cfg.pivot;

import com.activeviam.apps.constants.AppConstants;
import com.activeviam.copper.ICopperContext;
import com.activeviam.copper.api.Copper;
import com.activeviam.copper.api.CopperLevel;
import com.qfs.agg.IAggregationFunction;
import com.qfs.serialization.SerializableFunction;
import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.Collectors;


public class Measures {

	public static void build(final ICopperContext context) {
		Copper.agg(AppConstants.BENCHMARK_V1, IAggregationFunction.SINGLE_VALUE_PLUGIN_KEY)
				.as(AppConstants.BENCHMARK_V1).publish(context);
		Copper.agg(AppConstants.BENCHMARK_V2, IAggregationFunction.SINGLE_VALUE_PLUGIN_KEY)
				.as(AppConstants.BENCHMARK_V2).publish(context);
		Copper.agg(AppConstants.BENCHMARK_V3, IAggregationFunction.SINGLE_VALUE_PLUGIN_KEY)
				.as(AppConstants.BENCHMARK_V3).publish(context);
		Copper.agg(AppConstants.BENCHMARK_V4, IAggregationFunction.SINGLE_VALUE_PLUGIN_KEY)
				.as(AppConstants.BENCHMARK_V4).publish(context);
		Copper.agg(AppConstants.BENCHMARK_V5, IAggregationFunction.SINGLE_VALUE_PLUGIN_KEY)
				.as(AppConstants.BENCHMARK_V5).publish(context);
		Copper.agg(AppConstants.BENCHMARK_V6, IAggregationFunction.SINGLE_VALUE_PLUGIN_KEY)
				.as(AppConstants.BENCHMARK_V6).publish(context);
		Copper.agg(AppConstants.BENCHMARK_V7, IAggregationFunction.SINGLE_VALUE_PLUGIN_KEY)
				.as(AppConstants.BENCHMARK_V7).publish(context);
		Copper.agg(AppConstants.BENCHMARK_V8, IAggregationFunction.SINGLE_VALUE_PLUGIN_KEY)
				.as(AppConstants.BENCHMARK_V8).publish(context);
		Copper.agg(AppConstants.BENCHMARK_V9, IAggregationFunction.SINGLE_VALUE_PLUGIN_KEY)
				.as(AppConstants.BENCHMARK_V9).publish(context);
		Copper.agg(AppConstants.BENCHMARK_V10, IAggregationFunction.SINGLE_VALUE_PLUGIN_KEY)
				.as(AppConstants.BENCHMARK_V10).publish(context);
		Copper.agg(AppConstants.BENCHMARK_V1_E, IAggregationFunction.SINGLE_VALUE_PLUGIN_KEY)
				.as(AppConstants.BENCHMARK_V1_E).publish(context);
		Copper.agg(AppConstants.BENCHMARK_V2_E, IAggregationFunction.SINGLE_VALUE_PLUGIN_KEY)
				.as(AppConstants.BENCHMARK_V2_E).publish(context);
		Copper.agg(AppConstants.BENCHMARK_V3_E, IAggregationFunction.SINGLE_VALUE_PLUGIN_KEY)
				.as(AppConstants.BENCHMARK_V3_E).publish(context);
		Copper.agg(AppConstants.BENCHMARK_V4_E, IAggregationFunction.SINGLE_VALUE_PLUGIN_KEY)
				.as(AppConstants.BENCHMARK_V4_E).publish(context);
		Copper.agg(AppConstants.BENCHMARK_V5_E, IAggregationFunction.SINGLE_VALUE_PLUGIN_KEY)
				.as(AppConstants.BENCHMARK_V5_E).publish(context);
		Copper.agg(AppConstants.BENCHMARK_V6_E, IAggregationFunction.SINGLE_VALUE_PLUGIN_KEY)
				.as(AppConstants.BENCHMARK_V6_E).publish(context);
		Copper.agg(AppConstants.BENCHMARK_V7_E, IAggregationFunction.SINGLE_VALUE_PLUGIN_KEY)
				.as(AppConstants.BENCHMARK_V7_E).publish(context);
		Copper.agg(AppConstants.BENCHMARK_V8_E, IAggregationFunction.SINGLE_VALUE_PLUGIN_KEY)
				.as(AppConstants.BENCHMARK_V8_E).publish(context);
		Copper.agg(AppConstants.BENCHMARK_V9_E, IAggregationFunction.SINGLE_VALUE_PLUGIN_KEY)
				.as(AppConstants.BENCHMARK_V9_E).publish(context);
		Copper.agg(AppConstants.BENCHMARK_V10_E, IAggregationFunction.SINGLE_VALUE_PLUGIN_KEY)
				.as(AppConstants.BENCHMARK_V10_E).publish(context);

		// Benchmark-related measures

		final SerializableFunction<Object, Object> stringJoiner = o -> {
			if (o instanceof String[]) {
				return Arrays.stream(((String[]) o)).collect(Collectors.joining(" ;"));
			} else {
				return o;
			}
		};

		Copper
				.agg(AppConstants.BENCHMARK_INFO_DESCRIPTION, IAggregationFunction.SINGLE_VALUE_PLUGIN_KEY)
				.per(
						Copper.level(AppConstants.BENCHMARK_KEY_LEVEL))
				.doNotAggregateAbove()
				.as("BENCHMARK DESCRIPTION")
				.publish(context);

		Copper.agg(AppConstants.BENCHMARK_INFO_PARAMETER_NAMES,
				IAggregationFunction.SINGLE_VALUE_PLUGIN_KEY)
				.per(
						Copper.level(AppConstants.BENCHMARK_KEY_LEVEL))
				.doNotAggregateAbove()
				.as("BENCHMARK PARAMETERS")
				.publish(context);

		Copper
				.agg(AppConstants.BENCHMARK_INFO_VALUES_NAMES, IAggregationFunction.SINGLE_VALUE_PLUGIN_KEY)
				.per(
						Copper.level(AppConstants.BENCHMARK_KEY_LEVEL))
				.doNotAggregateAbove()
				.as("BENCHMARK METRICS")
				.publish(context);

		// Fetch description
		Copper.storeLookup(AppConstants.BENCHMARK_INFO_STORE_NAME)
				.withMapping(AppConstants.BENCHMARK_INFO_PLUGIN_KEY,
						Copper.level(AppConstants.BENCHMARK_KEY_LEVEL))
				.valueOf(AppConstants.BENCHMARK_INFO_DESCRIPTION)
				.as("Description of the current Benchmark").publish(context);

      Copper.member(AppConstants.BENCHMARK_KEY_LEVEL).map(o -> o+ System.lineSeparator()+"Zobi")
          .as("Fake Description of the current Benchmark").publish(context);
		// Fetch parameters
		Copper.storeLookup(AppConstants.BENCHMARK_INFO_STORE_NAME)
				.withMapping(AppConstants.BENCHMARK_INFO_PLUGIN_KEY,
						Copper.level(AppConstants.BENCHMARK_KEY_LEVEL))
				.valueOf(AppConstants.BENCHMARK_INFO_PARAMETER_NAMES)
				.map(stringJoiner)
				.as("Parameters of the current Benchmark").publish(context);

		Copper.storeLookup(AppConstants.BENCHMARK_INFO_STORE_NAME)
				.withMapping(AppConstants.BENCHMARK_INFO_PLUGIN_KEY,
						Copper.level(AppConstants.BENCHMARK_KEY_LEVEL))
				.valueOf(AppConstants.BENCHMARK_INFO_VALUES_NAMES)
				.map(stringJoiner)
				.as("Metrics of the Current Benchmark").publish(context);

		//Session Related measures
	}

}
