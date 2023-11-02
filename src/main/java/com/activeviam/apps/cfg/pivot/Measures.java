package com.activeviam.apps.cfg.pivot;

import com.activeviam.copper.ICopperContext;
import com.activeviam.copper.api.Copper;

import static com.activeviam.apps.cfg.pivot.PivotManagerConfig.DOUBLE_FORMATTER;
import static com.activeviam.apps.constants.StoreAndFieldConstants.NOTIONAL;

public class Measures {

    public static void build(final ICopperContext context) {
        Copper.sum(NOTIONAL)
                .as(NOTIONAL)
                .withFormatter(DOUBLE_FORMATTER)
                .publish(context);
    }
}