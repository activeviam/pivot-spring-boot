package com.activeviam.apps.cfg.pivot;

import static com.activeviam.apps.cfg.pivot.PivotManagerConfig.DOUBLE_FORMATTER;
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
