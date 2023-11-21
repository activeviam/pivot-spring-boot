/*
 * Copyright (C) ActiveViam 2023
 * ALL RIGHTS RESERVED. This material is the CONFIDENTIAL and PROPRIETARY
 * property of ActiveViam Limited. Any unauthorized use,
 * reproduction or transfer of this material is strictly prohibited
 */

package com.activeviam.apps.cfg.snowflake;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.activeviam.apps.cfg.IDatabaseSchemaConfig;
import com.activeviam.apps.constants.StoreAndFieldConstants;
import com.activeviam.database.sql.api.schema.SqlTableId;
import com.activeviam.directquery.api.INamingConvention;
import com.activeviam.directquery.snowflake.api.Schema;
import com.activeviam.directquery.snowflake.api.Table;
import com.activeviam.fwk.ActiveViamRuntimeException;
import com.google.common.base.CaseFormat;

@Configuration
@Profile("snowflake")
public class SnowflakeSchemaConfig implements IDatabaseSchemaConfig {

    public static final String TEST_DATABASE_NAME = "TEST_RESOURCES";
    public static final String TEST_SCHEMA_NAME = "DEMO";
    public static final String TABLE_NAME = "TRADES";
    public static final Map<String, String> mapping = Map.of(
            "TRADEID", "TradeID",
            "AsOf", "AsOf",
            "NOTIONAL", "Notional"
    );

    @Autowired
    private SnowflakeSessionConfig databaseSessionConfig;

    @Bean
    @Override
    public Schema getSchema() {
        final String externalTableName = "TRADES";
        Logger.getLogger(SnowflakeApplicationConfig.class.getName())
                .info(() -> "Loading data from table " + externalTableName);
        final Table table =
                databaseSessionConfig.session().discoverTable(new SqlTableId(TEST_DATABASE_NAME, TEST_SCHEMA_NAME, externalTableName))
                        .renameLocally(StoreAndFieldConstants.TRADES_STORE_NAME);

        return Schema.builder()
                .withExternalTables(List.of(table), List.of())
                .build();
    }

}
