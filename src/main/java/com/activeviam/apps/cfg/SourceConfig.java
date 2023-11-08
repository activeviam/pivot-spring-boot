/*
 * Copyright (C) ActiveViam 2023
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
import org.springframework.security.core.parameters.P;

import com.activeviam.apps.constants.StoreAndFieldConstants;
import com.qfs.msg.csv.ICSVParserConfiguration;
import com.qfs.msg.csv.ICSVSource;
import com.qfs.msg.csv.ICSVTopic;
import com.qfs.msg.csv.filesystem.impl.FileSystemCSVTopicFactory;
import com.qfs.msg.csv.impl.CSVParserConfiguration;
import com.qfs.msg.csv.impl.CSVSource;
import com.qfs.msg.csv.impl.CSVSourceConfiguration;
import com.qfs.platform.IPlatform;
import com.qfs.source.impl.CSVMessageChannelFactory;
import com.qfs.store.IDatastore;
import com.qfs.store.IDatastoreSchemaMetadata;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class SourceConfig {
    public static final String TRADES_TOPIC = "Trades";

    private final Environment env;
    private final IDatastore datastore;


    @Bean(destroyMethod = "close")
    public ICSVSource<Path> csvSource() {
        // TODO
        return null;
    }

    @Bean
    public CSVMessageChannelFactory<Path> csvChannelFactory() {
        // TODO
        return null;
    }


}
