package com.activeviam.apps.cfg.pivot;

import com.activeviam.copper.ICopperContext;
import com.activeviam.copper.api.Copper;

import static com.activeviam.apps.cfg.pivot.PivotManagerConfig.DOUBLE_FORMATTER;
import static com.activeviam.apps.constants.StoreAndFieldConstants.*;

public class Measures {

    public static void build(final ICopperContext context) {

        var store = Copper.store(SCENARIO_STORE_NAME).joinToCube();

        Copper.newHierarchy(SCENARIO)
                .fromStore(store)
                .slicing()
                .withLevel(SCENARIO)
                .withLevel(TREATMENT)
                .publish(context);

        Copper.sum(TRADES_NOTIONAL)
                .as(TRADES_NOTIONAL)
                .withFormatter(DOUBLE_FORMATTER)
                .publish(context);
    }
}
