/*
 * Copyright (C) ActiveViam 2023-2024
 * ALL RIGHTS RESERVED. This material is the CONFIDENTIAL and PROPRIETARY
 * property of ActiveViam Limited. Any unauthorized use,
 * reproduction or transfer of this material is strictly prohibited
 */
package com.activeviam.apps.cfg.pivot;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.activeviam.apps.constants.StoreAndFieldConstants;
import com.activeviam.cube.configurators.CalculationsConfigurator;
import com.activeviam.cube.configurators.CubeConfigurator;
import com.activeviam.cube.configurators.DimensionsConfigurator;
import com.activeviam.cube.configurators.SchemaSelectionConfigurator;
import com.activeviam.cube.configurators.impl.AtotiConfigurationUtils;
import com.quartetfs.biz.pivot.context.IContextValue;
import com.quartetfs.biz.pivot.context.drillthrough.impl.DrillthroughProperties;
import com.quartetfs.biz.pivot.context.impl.MdxContext;
import com.quartetfs.biz.pivot.context.impl.QueriesTimeLimit;

@Configuration
public class CubeConfiguration {

    public static final String CUBE_NAME = "Cube";

    public static final String SELECTION = "Selection";

    @Bean
    Collection<IContextValue> shareContextValues() {
        return List.of(
                QueriesTimeLimit.of(30, TimeUnit.SECONDS),
                MdxContext.builder().aggressiveFormulaEvaluation(true).end(),
                DrillthroughProperties.builder().withMaxRows(10000).build());
    }

    @Bean
    public CubeConfigurator cubeConfigurator(
            DimensionsConfigurator dimensionsConfigurator,
            CalculationsConfigurator calculationsConfigurator,
            Collection<IContextValue> sharedContextValues) {
        return AtotiConfigurationUtils.cubeConfigurator()
                .withName(CUBE_NAME)
                .withDimensionsConfigurator(dimensionsConfigurator)
                .withMeasuresConfigurator(calculationsConfigurator)
                .withSharedContextValues(sharedContextValues)
                .build();
    }

    @Bean
    public SchemaSelectionConfigurator schemaSelectionConfigurator(CubeConfigurator cubeConfigurator) {
        return AtotiConfigurationUtils.schemaSelectionConfigurator()
                .withSelectionSchema(SELECTION)
                .withBaseStore(StoreAndFieldConstants.TRADES_STORE_NAME)
                .withCubeConfigurators(List.of(cubeConfigurator))
                .build();
    }
}
