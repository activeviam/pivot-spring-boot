package com.activeviam.apps.cfg.pivot;

import com.activeviam.copper.ICopperContext;
import com.activeviam.copper.api.Copper;

import static com.activeviam.apps.constants.StoreAndFieldConstants.TRADES__NOTIONAL;

public class Measures {

    /* ********** */
    /* Formatters */
    /* ********** */
    public static final String DOUBLE_FORMATTER = "DOUBLE[#,###.##]";

    public static void build(final ICopperContext context) {
        Copper.sum(TRADES__NOTIONAL)
                .as(TRADES__NOTIONAL)
                .withFormatter(DOUBLE_FORMATTER)
                .publish(context);
    }

}
