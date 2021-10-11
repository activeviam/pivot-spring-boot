package com.activeviam.apps.cfg.pivot;

import com.activeviam.apps.constants.AppConstants;
import com.activeviam.copper.ICopperContext;
import com.activeviam.copper.api.Copper;
import com.qfs.agg.IAggregationFunction;


public class Measures {

    public static void build(final ICopperContext context) {
        Copper.agg(AppConstants.BENCHMARK_V1, IAggregationFunction.SINGLE_VALUE_PLUGIN_KEY).as(AppConstants.BENCHMARK_V1).publish(context);
        Copper.agg(AppConstants.BENCHMARK_V2, IAggregationFunction.SINGLE_VALUE_PLUGIN_KEY).as(AppConstants.BENCHMARK_V2).publish(context);
        Copper.agg(AppConstants.BENCHMARK_V3, IAggregationFunction.SINGLE_VALUE_PLUGIN_KEY).as(AppConstants.BENCHMARK_V3).publish(context);
        Copper.agg(AppConstants.BENCHMARK_V4, IAggregationFunction.SINGLE_VALUE_PLUGIN_KEY).as(AppConstants.BENCHMARK_V4).publish(context);
        Copper.agg(AppConstants.BENCHMARK_V5, IAggregationFunction.SINGLE_VALUE_PLUGIN_KEY).as(AppConstants.BENCHMARK_V5).publish(context);
        Copper.agg(AppConstants.BENCHMARK_V6, IAggregationFunction.SINGLE_VALUE_PLUGIN_KEY).as(AppConstants.BENCHMARK_V6).publish(context);
        Copper.agg(AppConstants.BENCHMARK_V7, IAggregationFunction.SINGLE_VALUE_PLUGIN_KEY).as(AppConstants.BENCHMARK_V7).publish(context);
        Copper.agg(AppConstants.BENCHMARK_V8, IAggregationFunction.SINGLE_VALUE_PLUGIN_KEY).as(AppConstants.BENCHMARK_V8).publish(context);
        Copper.agg(AppConstants.BENCHMARK_V9, IAggregationFunction.SINGLE_VALUE_PLUGIN_KEY).as(AppConstants.BENCHMARK_V9).publish(context);
        Copper.agg(AppConstants.BENCHMARK_V10, IAggregationFunction.SINGLE_VALUE_PLUGIN_KEY).as(AppConstants.BENCHMARK_V10).publish(context);

        // Benchmark-related measures
        Copper.agg(AppConstants.BENCHMARK_INFO_DESCRIPTION,IAggregationFunction.SINGLE_VALUE_PLUGIN_KEY)
            .per(
                Copper.level(AppConstants.BENCHMARK_KEY_LEVEL))
            .doNotAggregateAbove()
            .as("BENCHMARK DESCRIPTION")
            .publish(context);

        Copper.agg(AppConstants.BENCHMARK_INFO_PARAMETER_NAMES,IAggregationFunction.SINGLE_VALUE_PLUGIN_KEY)
            .per(
                Copper.level(AppConstants.BENCHMARK_KEY_LEVEL))
            .doNotAggregateAbove()
            .as("BENCHMARK PARAMETERS")
            .publish(context);

        Copper.agg(AppConstants.BENCHMARK_INFO_VALUES_NAMES,IAggregationFunction.SINGLE_VALUE_PLUGIN_KEY)
            .per(
                Copper.level(AppConstants.BENCHMARK_KEY_LEVEL))
            .doNotAggregateAbove()
            .as("BENCHMARK METRICS")
            .publish(context);

        //Session Related measures
    }

}
