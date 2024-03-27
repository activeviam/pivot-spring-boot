/*
 * Copyright (C) ActiveViam 2023-2024
 * ALL RIGHTS RESERVED. This material is the CONFIDENTIAL and PROPRIETARY
 * property of ActiveViam Limited. Any unauthorized use,
 * reproduction or transfer of this material is strictly prohibited
 */
package com.activeviam.apps.cfg.pivot;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.activeviam.cube.configurators.SchemaSelectionConfigurator;
import com.activeviam.cube.configurators.impl.MultiSchemaActivePivotManagerDescriptionConfigurator;
import com.qfs.server.cfg.IActivePivotManagerDescriptionConfig;
import com.qfs.server.cfg.IDatastoreSchemaDescriptionConfig;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Configuration
public class ActivePivotManagerConfiguration {

    /* *********************/
    /* OLAP Property names */
    /* *********************/

    public static final String MANAGER_NAME = "Manager";
    public static final String CATALOG_NAME = "Catalog";

    @Bean
    IActivePivotManagerDescriptionConfig activePivotManagerDescriptionConfig(
            IDatastoreSchemaDescriptionConfig datastoreSchemaDescriptionConfig,
            List<SchemaSelectionConfigurator> schemaConfigurators) {
        return new MultiSchemaActivePivotManagerDescriptionConfigurator(
                MANAGER_NAME, CATALOG_NAME, datastoreSchemaDescriptionConfig, schemaConfigurators);
    }
}
