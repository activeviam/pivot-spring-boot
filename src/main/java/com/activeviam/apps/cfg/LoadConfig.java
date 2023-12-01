/*
 * Copyright (C) ActiveViam 2023
 * ALL RIGHTS RESERVED. This material is the CONFIDENTIAL and PROPRIETARY
 * property of ActiveViam Limited. Any unauthorized use,
 * reproduction or transfer of this material is strictly prohibited
 */
package com.activeviam.apps.cfg;

import static com.activeviam.apps.cfg.SourceConfig.TRADES_TOPIC;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

import com.activeviam.apps.constants.StoreAndFieldConstants;
import com.qfs.gui.impl.JungSchemaPrinter;
import com.qfs.msg.IMessageChannel;
import com.qfs.msg.csv.IFileInfo;
import com.qfs.msg.csv.ILineReader;
import com.qfs.msg.csv.impl.CSVSource;
import com.qfs.source.impl.CSVMessageChannelFactory;
import com.qfs.store.IDatastore;
import com.qfs.store.impl.SchemaPrinter;
import com.qfs.util.timing.impl.StopWatch;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LoadConfig {
    private final IDatastore datastore;
    private final CSVSource<Path> csvSource;
    private final CSVMessageChannelFactory<Path> csvChannelFactory;
    private final boolean schemaPrinter;

    LoadConfig(
            IDatastore datastore,
            CSVSource<Path> csvSource,
            CSVMessageChannelFactory<Path> csvChannelFactory,
            @Value("${schema.printer:true}") boolean schemaPrinter) {
        this.datastore = datastore;
        this.csvSource = csvSource;
        this.csvChannelFactory = csvChannelFactory;
        this.schemaPrinter = schemaPrinter;
    }

    @EventListener(value = ApplicationReadyEvent.class)
    void onApplicationReady() throws Exception {
        log.info("ApplicationReadyEvent triggered");
        initialLoad();
    }

    private void initialLoad() throws Exception {
        log.info("Initial data load started.");
        final Collection<IMessageChannel<IFileInfo<Path>, ILineReader>> csvChannels = new ArrayList<>();
        csvChannels.add(csvChannelFactory.createChannel(TRADES_TOPIC, StoreAndFieldConstants.TRADES_STORE_NAME));

        // do the transactions
        final var before = System.nanoTime();

        datastore.edit(t -> {
            csvSource.fetch(csvChannels);
            t.forceCommit();
        });

        final var elapsed = System.nanoTime() - before;
        log.info("Initial data load completed in {} ms.", elapsed / 1000000L);

        printStoreSizes();
    }

    private void printStoreSizes() {
        // add some logging
        if (schemaPrinter) {
            // display the graph
            System.setProperty("java.awt.headless", "false");
            new JungSchemaPrinter(false).print("Datastore", datastore);
        }

        // Print stop watch profiling
        final var timingsOutput = new StringBuilder();
        StopWatch.get().appendTimings(timingsOutput);
        log.info(timingsOutput.toString());
        StopWatch.get().printTimingLegend();

        // print sizes
        SchemaPrinter.printStoresSizes(datastore.getMostRecentVersion());
    }
}
