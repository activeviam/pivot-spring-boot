/*
 * Copyright (C) ActiveViam 2023
 * ALL RIGHTS RESERVED. This material is the CONFIDENTIAL and PROPRIETARY
 * property of ActiveViam Limited. Any unauthorized use,
 * reproduction or transfer of this material is strictly prohibited
 */

package com.activeviam.apps.cfg.snowflake;

import java.util.Objects;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.activeviam.database.snowflake.api.SnowflakeDirectQuerySettings;
import com.activeviam.database.snowflake.api.SnowflakeProperties;
import com.activeviam.directquery.snowflake.api.Session;

import net.snowflake.client.core.SFSessionProperty;

@Configuration
@Profile("snowflake")
public class SnowflakeSessionConfig {

    public static final String TEST_CONNECTION_STRING =
            "jdbc:snowflake://am04499.europe-west4.gcp.snowflakecomputing.com";
    public static final String USERNAME_ENV_VARIABLE = "SNOWFLAKE_USERNAME";
    public static final String PASSWORD_ENV_VARIABLE = "SNOWFLAKE_PASSWORD";
    public static final String TEST_DATABASE_NAME = "BENCHMARK";
    public static final String TEST_SCHEMA_NAME = "DATALAKE";

    @Bean
    public Session session() {
        final String connectionString = TEST_CONNECTION_STRING + "/?user=" + getUsername() + "&db="
                + TEST_DATABASE_NAME + "&schema=" + TEST_SCHEMA_NAME;
        final SnowflakeProperties properties = SnowflakeProperties.builder()
                .connectionString(connectionString)
                .additionalOption(SFSessionProperty.PASSWORD, getPassword())
                .warehouse(selectWarehouse())
                .build();

        return Session.createSession(properties, SnowflakeDirectQuerySettings.defaults());
    }

    private static String getUsername() {
        return getRequiredEnvValue(USERNAME_ENV_VARIABLE);
    }

    private static String getPassword() {
        return getRequiredEnvValue(PASSWORD_ENV_VARIABLE);
    }

    private static String getRequiredEnvValue(final String name) {
        return Objects.requireNonNull(System.getenv(name), () -> "Missing env variable: " + name);
    }

    private static String selectWarehouse() {
        return "COMPUTE_WH";
    }
}
