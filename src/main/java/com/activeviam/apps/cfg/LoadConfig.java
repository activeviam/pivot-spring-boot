/*
 * Copyright (C) ActiveViam 2023-2024
 * ALL RIGHTS RESERVED. This material is the CONFIDENTIAL and PROPRIETARY
 * property of ActiveViam Limited. Any unauthorized use,
 * reproduction or transfer of this material is strictly prohibited
 */
package com.activeviam.apps.cfg;

import static com.activeviam.apps.cfg.westpac.FoundrySourceConfig.TRADE_PNL_TOPIC;

import java.util.Collections;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

import com.activeviam.apps.constants.StoreAndFieldConstants;
import com.qfs.msg.jdbc.IJDBCSource;
import com.qfs.source.impl.ArrayJDBCMessageChannelFactory;
import com.qfs.store.IDatastore;
import com.qfs.store.impl.SchemaPrinter;
import com.qfs.util.timing.impl.StopWatch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class LoadConfig {
    private final IDatastore datastore;
    private final ArrayJDBCMessageChannelFactory jdbcChannelFactory;
    private final IJDBCSource<Object[]> jdbcSource;

    @EventListener(value = ApplicationReadyEvent.class)
    void onApplicationReady() throws Exception {
        log.info("ApplicationReadyEvent triggered");
        initialLoad();
    }

    private void initialLoad() {
        log.info("Initial data load started.");
        // JDBC channel
        var jdbcChannel = jdbcChannelFactory.createChannel(TRADE_PNL_TOPIC,
                StoreAndFieldConstants.TRADE_PNL_STORE_NAME);

        // do the transactions
        var before = System.nanoTime();

        datastore.edit(t -> {
            jdbcSource.fetch(Collections.singleton(jdbcChannel));
            t.forceCommit();
        });

        var elapsed = System.nanoTime() - before;
        log.info("Initial data load completed in {} ms.", elapsed / 1000000L);

        printStoreSizes();
    }

    private void printStoreSizes() {
        // Print stop watch profiling
        var timingsOutput = new StringBuilder();
        StopWatch.get().appendTimings(timingsOutput);
        log.info(timingsOutput.toString());
        StopWatch.get().printTimingLegend();

        // print sizes
        SchemaPrinter.printStoresSizes(datastore.getMasterHead());
    }
}
