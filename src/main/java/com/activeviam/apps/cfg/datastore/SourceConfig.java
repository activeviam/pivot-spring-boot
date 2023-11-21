/*
 * Copyright (C) ActiveViam 2023
 * ALL RIGHTS RESERVED. This material is the CONFIDENTIAL and PROPRIETARY
 * property of ActiveViam Limited. Any unauthorized use,
 * reproduction or transfer of this material is strictly prohibited
 */
package com.activeviam.apps.cfg.datastore;

import java.nio.file.Path;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;

import com.activeviam.apps.constants.StoreAndFieldConstants;
import com.qfs.msg.csv.ICSVParserConfiguration;
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
@Profile("datastore")
public class SourceConfig {
    public static final String TRADES_TOPIC = "Trades";

    private final Environment env;
    private final IDatastore datastore;

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
    public CSVSource<Path> csvSource() {
        var schemaMetadata = datastore.getQueryMetadata().getMetadata();
        var csvTopicFactory = csvTopicFactory();
        var csvSource = new CSVSource<Path>();

        var tradesColumns = schemaMetadata.getFields(StoreAndFieldConstants.TRADES_STORE_NAME);
        var tradesTopic = csvTopicFactory.createTopic(
                TRADES_TOPIC, env.getProperty("file.trades"), createParserConfig(tradesColumns.size(), tradesColumns));
        csvSource.addTopic(tradesTopic);

        // Allocate half the machine cores to CSV parsing
        var parserThreads = Math.max(2, IPlatform.CURRENT_PLATFORM.getProcessorCount() / 2);
        log.info("Allocating " + parserThreads + " parser threads.");

        var sourceConfigurationBuilder = new CSVSourceConfiguration.CSVSourceConfigurationBuilder<Path>();
        sourceConfigurationBuilder.parserThreads(parserThreads);
        sourceConfigurationBuilder.synchronousMode(Boolean.valueOf(env.getProperty("synchronousMode", "false")));
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
