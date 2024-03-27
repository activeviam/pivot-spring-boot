/*
 * Copyright (C) ActiveViam 2024
 * ALL RIGHTS RESERVED. This material is the CONFIDENTIAL and PROPRIETARY
 * property of ActiveViam Limited. Any unauthorized use,
 * reproduction or transfer of this material is strictly prohibited
 */

package com.activeviam.apps.cfg.pivot;

import java.util.Collection;
import java.util.List;

import org.springframework.context.annotation.Configuration;

import com.activeviam.apps.constants.StoreAndFieldConstants;
import com.activeviam.cube.configurators.DimensionDefinitions;
import com.activeviam.cube.configurators.DimensionsConfigurator;
import com.activeviam.cube.configurators.impl.AtotiConfigurationUtils;
import com.quartetfs.biz.pivot.cube.dimension.IDimension;
import com.quartetfs.biz.pivot.cube.hierarchy.ILevelInfo;
import com.quartetfs.fwk.ordering.impl.ReverseOrderComparator;

@Configuration
public class DimensionsConfiguration implements DimensionsConfigurator {
    @Override
    public Collection<DimensionDefinitions> dimensions() {
        return List.of(
                AtotiConfigurationUtils.dimensionDefinitions(b -> b.withDimension(StoreAndFieldConstants.ASOFDATE)
                        .withType(IDimension.DimensionType.TIME)
                        .withHierarchy(StoreAndFieldConstants.ASOFDATE)
                        .slicing()
                        .withLevelOfSameName()
                        .withType(ILevelInfo.LevelType.TIME)
                        .withComparator(ReverseOrderComparator.type)));
    }
}
