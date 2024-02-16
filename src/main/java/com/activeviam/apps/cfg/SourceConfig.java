/*
 * Copyright (C) ActiveViam 2023-2024
 * ALL RIGHTS RESERVED. This material is the CONFIDENTIAL and PROPRIETARY
 * property of ActiveViam Limited. Any unauthorized use,
 * reproduction or transfer of this material is strictly prohibited
 */
package com.activeviam.apps.cfg;

import java.nio.file.Path;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import com.activeviam.apps.constants.StoreAndFieldConstants;
import com.qfs.msg.csv.ICSVParserConfiguration;
import com.qfs.msg.csv.ICSVSource;
import com.qfs.msg.csv.filesystem.impl.FileSystemCSVTopicFactory;
import com.qfs.msg.csv.impl.CSVParserConfiguration;
import com.qfs.msg.csv.impl.CSVSource;
import com.qfs.msg.csv.impl.CSVSourceConfiguration;
import com.qfs.platform.IPlatform;
import com.qfs.source.impl.CSVMessageChannelFactory;
import com.qfs.store.IDatastore;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class SourceConfig {
    public static final String TRADES_TOPIC = "Trades";

    private final Environment env;
    private final IDatastore datastore;

    private static ICSVSource<Path> createCsvSource() {
        return new CSVSource<Path>();
    }

    /**
     * Topic factory bean. Allows to create CSV topics and watch changes to directories. Autocloseable.
     *
     * @return the topic factory
     */
    @Bean
    public FileSystemCSVTopicFactory csvTopicFactory() {
        return new FileSystemCSVTopicFactory(false);
    }

    @Bean(destroyMethod = "close")
    public ICSVSource<Path> csvSource() {
        var tradesColumns = datastore
                .getEntityResolver()
                .getTable(StoreAndFieldConstants.TRADES_STORE_NAME)
                .getFieldNames();

        var csvSource = createCsvSource();
        var tradesTopic = csvTopicFactory()
                .createTopic(
                        TRADES_TOPIC,
                        env.getProperty("file.trades"),
                        createParserConfig(tradesColumns.size(), tradesColumns));
        csvSource.addTopic(tradesTopic);

        // Allocate half the machine cores to CSV parsing
        var parserThreads = Math.max(2, IPlatform.CURRENT_PLATFORM.getProcessorCount() / 2);
        log.info("Allocating {} parser threads.", parserThreads);

        var sourceConfigurationBuilder = new CSVSourceConfiguration.CSVSourceConfigurationBuilder<Path>();
        sourceConfigurationBuilder.parserThreads(parserThreads);
        sourceConfigurationBuilder.synchronousMode(
                Boolean.parseBoolean(env.getProperty("synchronousMode", Boolean.FALSE.toString())));
        csvSource.configure(sourceConfigurationBuilder.build());
        return csvSource;
    }

    @Bean
    public CSVMessageChannelFactory<Path> csvChannelFactory() {
        return new CSVMessageChannelFactory<>(csvSource(), datastore);
    }

    private ICSVParserConfiguration createParserConfig(int columnCount, List<String> columns) {
        var cfg = columns == null ? new CSVParserConfiguration(columnCount) : new CSVParserConfiguration(columns);
        cfg.setNumberSkippedLines(1); // skip the first line
        return cfg;
    }
}
