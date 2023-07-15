package com.activeviam.apps.cfg.pivot;

import com.activeviam.apps.constants.StoreAndFieldConstants;
import com.activeviam.desc.build.ICanBuildCubeDescription;
import com.activeviam.desc.build.dimensions.ICanStartBuildingDimensions;
import com.quartetfs.biz.pivot.cube.dimension.IDimension;
import com.quartetfs.biz.pivot.cube.hierarchy.ILevelInfo;
import com.quartetfs.biz.pivot.definitions.IActivePivotInstanceDescription;
import com.quartetfs.fwk.ordering.impl.ReverseOrderComparator;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;

@RequiredArgsConstructor
@Configuration
public class DimensionConfig {

    /**
     * Adds the dimensions descriptions to the input builder.
     *
     * @param builder
     *            The cube builder
     * @return The builder for chained calls
     */
    public ICanBuildCubeDescription<IActivePivotInstanceDescription> build(ICanStartBuildingDimensions builder) {

        return builder.withSingleLevelDimensions(StoreAndFieldConstants.TRADES_TRADEID)

                // Make the AsOfDate hierarchy slicing - we do not aggregate across dates
                // Also show the dates in reverse order ie most recent date first
                .withDimension(StoreAndFieldConstants.ASOFDATE)
                .withType(IDimension.DimensionType.TIME)
                .withHierarchy(StoreAndFieldConstants.ASOFDATE)
                .slicing()
                .withLevelOfSameName()
                .withType(ILevelInfo.LevelType.TIME)
                .withComparator(ReverseOrderComparator.type);
    }
}
