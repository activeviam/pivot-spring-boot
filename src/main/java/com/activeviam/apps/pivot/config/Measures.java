package com.activeviam.apps.pivot.config;

import static com.activeviam.apps.pivot.config.PivotManagerConfig.DOUBLE_FORMATTER;
import static com.activeviam.apps.constants.StoreAndFieldConstants.TRADES_NOTIONAL;


import com.activeviam.copper.ICopperContext;
import com.activeviam.copper.api.Copper;

public class Measures {

	public static void build(final ICopperContext context) {
		Copper.sum(TRADES_NOTIONAL)
				.as(TRADES_NOTIONAL)
				.withFormatter(DOUBLE_FORMATTER)
				.publish(context);
	}

}
