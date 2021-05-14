package com.activeviam.apps.cfg.pivot;

import com.activeviam.apps.pp.MultiplyAtLeafPostProcessor;
import com.activeviam.copper.ICopperContext;
import com.activeviam.copper.api.Copper;
import com.qfs.agg.impl.SingleValueFunction;
import com.qfs.agg.impl.SumFunction;

import static com.activeviam.apps.cfg.pivot.PivotManagerConfig.DOUBLE_FORMATTER;
import static com.activeviam.apps.constants.StoreAndFieldConstants.*;

public class Measures {

    public static void build(final ICopperContext context) {
        var pnl = Copper.sum(PNL)
                .as(PNL)
                .withFormatter(DOUBLE_FORMATTER)
                .publish(context);

        var deltaSum = Copper.sum(DELTA)
                .as(DELTA + ".SUM (incorrect)")
                .withFormatter(DOUBLE_FORMATTER)
                .publish(context);

        var delta = Copper.agg(DELTA, SingleValueFunction.PLUGIN_KEY)
                .per(Copper.level(INSTRUMENT_ID))
                .sum()
                .as(DELTA)
                .withFormatter(DOUBLE_FORMATTER)
                .publish(context);

        // Multiply delta by pnl at the instrument level
        delta.multiply(pnl)
                .per(Copper.level(INSTRUMENT_ID))
                .sum()
                .as("M1")
                .withFormatter(DOUBLE_FORMATTER)
                .publish(context);

        // Same as above, but add an extra .per() at the lowest level of the multi-level hierarchy
        delta.multiply(pnl)
                .per(Copper.level(INSTRUMENT_ID))
                .sum()
                .per(Copper.level(L3))
                .sum()
                .as("M2")
                .withFormatter(DOUBLE_FORMATTER)
                .publish(context);

        // This has one less retrieval - i think because it does the .per() in a single step whereas the above creates an intermediate measure
        delta.multiply(pnl)
                .per(Copper.level(INSTRUMENT_ID), Copper.level(L3))
                .sum()
                .as("M3")
                .withFormatter(DOUBLE_FORMATTER)
                .publish(context);

        // Instead of using a copper multiply, use a dynamic agg
        Copper.newPostProcessor(MultiplyAtLeafPostProcessor.PLUGIN_KEY)
                .withUnderlyingMeasures(delta, pnl)
                .withProperty(MultiplyAtLeafPostProcessor.LEAF_LEVELS, "InstrumentId@InstrumentId@InstrumentId")
                .as("M4")
                .withFormatter(DOUBLE_FORMATTER)
                .publish(context);


    }

}
