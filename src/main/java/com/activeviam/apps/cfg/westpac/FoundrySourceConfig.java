/*
 * Copyright (C) ActiveViam 2024
 * ALL RIGHTS RESERVED. This material is the CONFIDENTIAL and PROPRIETARY
 * property of ActiveViam Limited. Any unauthorized use,
 * reproduction or transfer of this material is strictly prohibited
 */

package com.activeviam.apps.cfg.westpac;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import com.activeviam.fwk.ActiveViamRuntimeException;
import com.qfs.msg.jdbc.IConnectionSupplier;
import com.qfs.msg.jdbc.IJDBCSource;
import com.qfs.msg.jdbc.impl.JDBCTopic;
import com.qfs.source.impl.ArrayJDBCMessageChannelFactory;
import com.qfs.store.IDatastore;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class FoundrySourceConfig {
    public static final String TRADE_PNL_TOPIC = "TRADE_PNL";
    private final WestpacProperties westpacProperties;
    private final IDatastore datastore;

    private static void loadJDBCDriver(String driverClassName) {
        try {
            Class.forName(driverClassName);
        } catch (Exception e) {
            throw new ActiveViamRuntimeException("Could not load the Jdbc driver " + driverClassName, e);
        }
    }

    @Bean
    public Void setupDB() throws Exception {
        loadJDBCDriver(westpacProperties.getJdbc().getDriver());
        try (var ignored = DriverManager.getConnection(
                westpacProperties.getJdbc().getUrl(),
                westpacProperties.getJdbc().getProperties())) {
            log.info("JDBC connection is successful.");
        }
        return null;
    }

    @Bean(destroyMethod = "close")
    @DependsOn(value = "setupDB")
    public IJDBCSource<Object[]> jdbcSource() {
        var source = IJDBCSource.builder()
                .arrayRows()
                .withConnectionSupplier(new PropertiesBasedConnectionSupplier(
                        westpacProperties.getJdbc().getDriver(),
                        westpacProperties.getJdbc().getUrl(),
                        westpacProperties.getJdbc().getProperties()))
                .build();
        source.addTopic(
                new JDBCTopic(TRADE_PNL_TOPIC, westpacProperties.getJdbc().getQuery()));
        log.info("Topic: [{}] added to jdbcSource.", TRADE_PNL_TOPIC);
        return source;
    }

    @Bean
    public ArrayJDBCMessageChannelFactory jdbcChannelFactory() {
        var channelFactory = new ArrayJDBCMessageChannelFactory(jdbcSource(), datastore);
        //        channelFactory.setCalculatedColumns(
        //                TRADE_PNL_TOPIC,
        //                TRADE_PNL_STORE_NAME,
        //                List.of(new DoubleArrayJdbcColumnCalculator(TRADE_PNL_PNL)));
        return channelFactory;
    }

    /**
     * This will pass all the properties to the Driver rather than just the username and password.
     */
    @RequiredArgsConstructor
    public static class PropertiesBasedConnectionSupplier implements IConnectionSupplier {
        private final String driver;
        private final String url;
        private final Properties info;

        @Override
        public Connection createConnection() throws SQLException {
            loadJDBCDriver(driver);
            return DriverManager.getConnection(url, info);
        }
    }
}
