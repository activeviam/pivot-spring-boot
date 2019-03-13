package com.activeviam.apps.pivotspringboot.activepivot;

import com.activeviam.desc.build.ICanStartBuildingMeasures;
import com.activeviam.desc.build.IHasAtLeastOneMeasure;

import static com.activeviam.apps.pivotspringboot.activepivot.StoreAndFieldConstants.TRADES__NOTIONAL;

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
