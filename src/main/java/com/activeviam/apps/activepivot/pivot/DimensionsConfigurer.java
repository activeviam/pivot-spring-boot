/*
 * (C) ActiveViam 2022
 * ALL RIGHTS RESERVED. This material is the CONFIDENTIAL and PROPRIETARY
 * property of ActiveViam. Any unauthorized use,
 * reproduction or transfer of this material is strictly prohibited
 */
package com.activeviam.apps.activepivot.pivot;

import com.activeviam.apps.activepivot.configurers.IDimensionsConfigurer;
import com.activeviam.desc.build.ICanBuildCubeDescription;
import com.activeviam.desc.build.dimensions.ICanStartBuildingDimensions;
import com.quartetfs.biz.pivot.cube.dimension.IDimension;
import com.quartetfs.biz.pivot.cube.hierarchy.ILevelInfo;
import com.quartetfs.biz.pivot.definitions.IActivePivotInstanceDescription;
import com.quartetfs.fwk.ordering.impl.ReverseOrderComparator;
import org.springframework.stereotype.Component;

import static com.activeviam.apps.activepivot.pivot.CubeConstants.AS_OF_DATE;
import static com.activeviam.apps.activepivot.pivot.CubeConstants.TRADE_ID;

/**
 * @author ActiveViam
 */
@Component
public class DimensionsConfigurer implements IDimensionsConfigurer {
    @Override
    public ICanBuildCubeDescription<IActivePivotInstanceDescription> add(ICanStartBuildingDimensions builder) {
        return builder.withSingleLevelDimensions(TRADE_ID)

                // Make the AsOfDate hierarchy slicing - we do not aggregate across dates
                // Also show the dates in reverse order ie most recent date first
                .withDimension(AS_OF_DATE)
                .withType(IDimension.DimensionType.TIME)
                .withHierarchy(AS_OF_DATE)
                .slicing()
                .withLevelOfSameName()
                .withType(ILevelInfo.LevelType.TIME)
                .withComparator(ReverseOrderComparator.type);
    }
}