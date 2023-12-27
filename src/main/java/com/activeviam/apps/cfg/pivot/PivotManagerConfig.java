/*
 * Copyright (C) ActiveViam 2023
 * ALL RIGHTS RESERVED. This material is the CONFIDENTIAL and PROPRIETARY
 * property of ActiveViam Limited. Any unauthorized use,
 * reproduction or transfer of this material is strictly prohibited
 */
package com.activeviam.apps.cfg.pivot;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import com.activeviam.apps.cfg.DatabaseSelectionConfig;
import com.activeviam.apps.cfg.PluginConfig;
import com.activeviam.builders.StartBuilding;
import com.qfs.server.cfg.IActivePivotManagerDescriptionConfig;
import com.quartetfs.biz.pivot.definitions.IActivePivotManagerDescription;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Configuration
@DependsOn(PluginConfig.BEAN_NAME)
public class PivotManagerConfig implements IActivePivotManagerDescriptionConfig {

    /* *********************/
    /* OLAP Property names */
    /* *********************/

    public static final String MANAGER_NAME = "Manager";
    public static final String CATALOG_NAME = "Catalog";
    public static final String SCHEMA_NAME = "Schema";

    /* ********** */
    /* Formatters */
    /* ********** */
    public static final String DOUBLE_FORMATTER = "DOUBLE[#,###.##]";
    public static final String INT_FORMATTER = "INT[#,###]";
    public static final String TIMESTAMP_FORMATTER = "DATE[HH:mm:ss]";

    public static final String NATIVE_MEASURES = "Native Measures";

    private final DatabaseSelectionConfig databaseSelectionConfig;
    private final DistributedCubeConfig cubeConfig;

    @Override
    public IActivePivotManagerDescription managerDescription() {
        if (cubeConfig.spec.isDatacube()) {
            return StartBuilding.managerDescription(MANAGER_NAME)
                    .withCatalog(CATALOG_NAME)
                    .containingAllCubes()
                    .withSchema(SCHEMA_NAME)
                    .withSelection(databaseSelectionConfig.createSchemaSelectionDescription())
                    .withCube(cubeConfig.createCubeDescription())
                    .build();
        } else {
            return StartBuilding.managerDescription(MANAGER_NAME)
                    .withCatalog(CATALOG_NAME)
                    .containingAllCubes()
                    .withDistributedCube(cubeConfig.createQueryCubeDescription())
                    .build();
        }
    }
}
