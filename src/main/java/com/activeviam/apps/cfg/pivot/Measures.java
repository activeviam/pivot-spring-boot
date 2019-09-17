package com.activeviam.apps.cfg.pivot;

import com.activeviam.desc.build.ICanStartBuildingMeasures;
import com.activeviam.desc.build.IHasAtLeastOneMeasure;

import static com.activeviam.apps.constants.StoreAndFieldConstants.TRADES__NOTIONAL;

public class Measures {

    /* ********** */
    /* Formatters */
    /* ********** */
    public static final String DOUBLE_FORMATTER = "DOUBLE[#,###.##]";

    public static IHasAtLeastOneMeasure build(final ICanStartBuildingMeasures builder) {
        return builder
                // Actual measures
                .withAggregatedMeasure()
                .sum(TRADES__NOTIONAL)
                .withName(TRADES__NOTIONAL)
                .withFormatter(DOUBLE_FORMATTER)
                ;
    }

}
