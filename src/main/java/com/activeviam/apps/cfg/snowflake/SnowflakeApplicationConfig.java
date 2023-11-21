/*
 * Copyright (C) ActiveViam 2023
 * ALL RIGHTS RESERVED. This material is the CONFIDENTIAL and PROPRIETARY
 * property of ActiveViam Limited. Any unauthorized use,
 * reproduction or transfer of this material is strictly prohibited
 */

package com.activeviam.apps.cfg.snowflake;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;

import com.activeviam.apps.cfg.pivot.CubeConfig;
import com.activeviam.apps.cfg.pivot.PivotManagerConfig;
import com.activeviam.directquery.snowflake.api.ASnowflakeConfig;
import com.activeviam.directquery.snowflake.api.Application;
import com.activeviam.directquery.snowflake.api.Schema;
import com.activeviam.directquery.snowflake.api.Session;

@Configuration
@Profile("snowflake")
public class SnowflakeApplicationConfig extends ASnowflakeConfig {

    @Autowired
    private Environment env;

    @Autowired
    private CubeConfig cubeConfig;

    @Autowired
    private SnowflakeSchemaConfig databaseSchemaConfig;

    @Autowired
    private SnowflakeSessionConfig databaseSessionConfig;

    @Autowired
    private PivotManagerConfig pivotManagerConfig;

    @Bean
    @Override
    public Application snowflakeApplication() {
        final Session session = databaseSessionConfig.session();
        final Schema schema = databaseSchemaConfig.getSchema();
        return session.applicationBuilder()
                .schema(schema)
                .managerDescription(pivotManagerConfig.managerDescription())
                .build();
    }

}