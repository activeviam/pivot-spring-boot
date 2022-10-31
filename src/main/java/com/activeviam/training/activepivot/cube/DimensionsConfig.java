package com.activeviam.training.activepivot.cube;

import com.activeviam.desc.build.dimensions.ICanStartBuildingDimensions;
import com.activeviam.training.constants.StoreAndFieldConstants;
import com.quartetfs.biz.pivot.cube.dimension.IDimension;
import com.quartetfs.biz.pivot.cube.hierarchy.ILevelInfo;
import com.quartetfs.fwk.ordering.impl.ReverseOrderComparator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DimensionsConfig {

    /**
     * Adds the dimensions descriptions to the input builder.
     *
     * @return The builder for chained calls
     */
    @Bean
    public ICanStartBuildingDimensions.DimensionsAdder dimensions() {
        return builder ->
                builder.withSingleLevelDimensions(StoreAndFieldConstants.TRADES_TRADEID)

                        // Make the AsOfDate hierarchy slicing - we do not aggregate across dates
                        // Also show the dates in reverse order ie most recent date first
                        .withDimension(StoreAndFieldConstants.ASOFDATE).withType(IDimension.DimensionType.TIME)
                        .withHierarchy(StoreAndFieldConstants.ASOFDATE).slicing()
                        .withLevelOfSameName()
                        .withType(ILevelInfo.LevelType.TIME).withComparator(ReverseOrderComparator.type)

                        .withSingleLevelDimension(StoreAndFieldConstants.INSTRUMENT_NAME);

    }

}
