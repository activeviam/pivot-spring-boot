package com.activeviam.apps.cfg.pivot;

import com.activeviam.copper.ICopperContext;
import com.activeviam.copper.api.Copper;
import com.activeviam.copper.store.Mapping;
import com.qfs.store.Types;

import static com.activeviam.apps.cfg.pivot.PivotManagerConfig.DOUBLE_FORMATTER;
import static com.activeviam.apps.constants.StoreAndFieldConstants.*;

public class Measures {

    public static void build(final ICopperContext context) {

        var groups = Copper.store(GROUPS_STORE_NAME)
                .joinToCube(Mapping.JoinType.LEFT)
                .withMapping(ASOFDATE)
                .withMapping(TRADES_TRADEID)
                .withDefaultValue(GROUP_ID, "undefined");

        Copper.newHierarchy(GROUP_TYPE, GROUP_TYPE)
                .fromStore(groups)
                // .slicing()
                .withLevel(GROUP_TYPE, WEIGHTS_STORE_NAME + "/" + GROUP_TYPE)
                .withLevel(GROUP_LVL_1, WEIGHTS_STORE_NAME + "/" + GROUP_LVL_1)
                .withLevel(GROUP_LVL_2, WEIGHTS_STORE_NAME + "/" + GROUP_LVL_2)
                .publish(context);

        Copper.newHierarchy(GROUP_ID, GROUP_ID)
                .fromStore(groups)
                //                .hidden()
                .withLevel(GROUP_ID)
                .publish(context);

        var weight = Copper.newLookupMeasure(groups.field(WEIGHTS_STORE_NAME + "/" + WEIGHT))
                // .per(GROUP_TYPE)
                .as(WEIGHT)
                .publish(context);

        var notional = Copper.sum(TRADES_NOTIONAL)
                .as(TRADES_NOTIONAL)
                .withFormatter(DOUBLE_FORMATTER)
                .publish(context);

        Copper.userDefinedAgg(notional, weight)
                .aggregationBuffer(Types.TYPE_DOUBLE)
                .contribute((measures, buffer) -> {
                    final var n = measures.readDouble(0);
                    final var w = measures.readDouble(1);
                    if (buffer.isNull(0)) {
                        buffer.write(0, n * w);
                    } else {
                        buffer.addDouble(0, n * w);
                    }
                })
                .merge((in, out) -> {
                    final var nw = in.readDouble(0);
                    out.addDouble(0, nw);
                })
                .outputFromBuffer(0)
                .per(Copper.level(GROUP_ID))
                .per(Copper.level(GROUP_TYPE))
                .doNotAggregateAbove()
                .as("Weighted notional UDAF")
                .publish(context);

        notional.multiply(weight)
                .per(Copper.level(GROUP_ID))
                .sum()
                .per(Copper.level(GROUP_TYPE))
                .doNotAggregateAbove()
                .as("Weighted notional")
                .publish(context);
    }
}
