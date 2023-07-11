package com.activeviam.apps.cfg.pivot;

import static com.activeviam.apps.cfg.pivot.PivotManagerConfig.DOUBLE_FORMATTER;
import static com.activeviam.apps.constants.StoreAndFieldConstants.TRADES_NOTIONAL;


import com.activeviam.copper.ICopperContext;
import com.activeviam.copper.api.Copper;
import com.activeviam.copper.api.CopperMeasure;
import com.qfs.literal.ILiteralType;

public class Measures {

	public static void build(final ICopperContext context) {
		CopperMeasure measure = Copper.sum(TRADES_NOTIONAL)
				.as(TRADES_NOTIONAL)
				.withFormatter(DOUBLE_FORMATTER)
				.publish(context);

		measure.map((reader, writer) -> {
			double value = reader.readDouble(0);
			if (Math.abs(value) < 0.00001) {
				writer.writeNull();
				return;
			}
			writer.writeDouble(value);
		}, ILiteralType.DOUBLE)
				.as("Notional filtered")
				.withFormatter(DOUBLE_FORMATTER)
				.publish(context);
	}

}
