package com.activeviam.apps.cfg.pivot;

import com.activeviam.copper.ICopperContext;
import com.activeviam.copper.api.Copper;

import static com.activeviam.apps.cfg.pivot.PivotManagerConfig.DOUBLE_FORMATTER;
import static com.activeviam.apps.constants.StoreAndFieldConstants.*;

public class Measures {

    public static void build(final ICopperContext context) {

        var portfolios = Copper.store(PORTFOLIOS_STORE_NAME)
                .joinToCube()
                .withMapping(ASOFDATE)
                .withMapping(TRADES_TRADEID)
                .withDefaultValue(GROUP_ID, "undefined");

        Copper.newHierarchy(PORTFOLIOS, PORTFOLIOS)
                .fromStore(portfolios)
                .slicing()
                .withLevel(PORTFOLIO_ROOT, PORTFOLIOS_STRUCT_STORE_NAME + "/" + PORTFOLIO_ROOT)
                .publish(context);

        Copper.newHierarchy(PORTFOLIOS, PORTFOLIO_LEVELS)
                .fromStore(portfolios)
                // .withLevel(PORTFOLIO_ROOT, PORTFOLIOS_STRUCT_STORE_NAME + "/" + PORTFOLIO_ROOT)
                .withLevel(PORTFOLIO_LVL_1, PORTFOLIOS_STRUCT_STORE_NAME + "/" + PORTFOLIO_LVL_1)
                .withLevel(PORTFOLIO_LVL_2, PORTFOLIOS_STRUCT_STORE_NAME + "/" + PORTFOLIO_LVL_2)
                .withLevel(PORTFOLIO_LVL_3, PORTFOLIOS_STRUCT_STORE_NAME + "/" + PORTFOLIO_LVL_3)
                .publish(context);

        Copper.newHierarchy(PORTFOLIOS, PORTFOLIO_ID)
                .fromStore(portfolios)
                // .hidden()
                .withLevel(PORTFOLIO_ID)
                .publish(context);

        var notional = Copper.sum(TRADES_NOTIONAL)
                .as(TRADES_NOTIONAL)
                .withFormatter(DOUBLE_FORMATTER)
                .publish(context);
    }
}
