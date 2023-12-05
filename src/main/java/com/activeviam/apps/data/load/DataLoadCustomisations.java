/*
 * Copyright (C) ActiveViam 2023
 * ALL RIGHTS RESERVED. This material is the CONFIDENTIAL and PROPRIETARY
 * property of ActiveViam Limited. Any unauthorized use,
 * reproduction or transfer of this material is strictly prohibited
 */

package com.activeviam.apps.data.load;

import static com.activeviam.apps.constants.StoreAndFieldConstants.ASOFDATE;
import static com.activeviam.apps.constants.StoreAndFieldConstants.CPTY;
import static com.activeviam.apps.constants.StoreAndFieldConstants.CPTY_STORE_NAME;
import static com.activeviam.apps.constants.StoreAndFieldConstants.INSTRUMENTS_STORE_NAME;
import static com.activeviam.apps.constants.StoreAndFieldConstants.SCOPE_CONSTANT;
import static com.activeviam.apps.constants.StoreAndFieldConstants.TRADES_STORE_NAME;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.activeviam.data.load.DataLoadConfiguration;
import com.activeviam.data.load.LambdaTuplePublisher;
import com.activeviam.data.load.ScopedColumnCalculator;
import com.activeviam.data.load.source.csv.CsvDatasourceConfiguration;
import com.activeviam.data.load.source.csv.LambdaCsvColumnParser;
import com.activeviam.data.load.source.csv.model.CsvStoresDataLoadConfigurer;
import com.activeviam.fwk.ActiveViamRuntimeException;
import com.qfs.msg.IColumnCalculator;

@Configuration
@Import({DataLoadConfiguration.class, CsvDatasourceConfiguration.class})
public class DataLoadCustomisations {

    public static final String CONSTANT_SCOPE_PARAMETER = "CONSTANT";

    public static final String AS_OF_DATE_SCOPE_PARAMETER = "AS_OF_DATE";

    private static IColumnCalculator asOfDateColumnCalculator() {
        return new ScopedColumnCalculator<>(ASOFDATE, (context, scope) -> {
            if (scope.hasParameter(AS_OF_DATE_SCOPE_PARAMETER)) {
                return scope.get(AS_OF_DATE_SCOPE_PARAMETER);
            } else {
                throw new ActiveViamRuntimeException("AsOfDate missing from scope, cannot load data");
            }
        });
    }

    @Bean
    Void csvDataLoadCustomisations(CsvStoresDataLoadConfigurer configurer) {
        configurer.configure(TRADES_STORE_NAME, config -> config.editColumns(cols -> {
                    cols.remove(ASOFDATE);
                    return cols;
                })
                .columnCalculator(asOfDateColumnCalculator())
                .columnCalculator(new LambdaCsvColumnParser(CPTY, val -> "CPTY " + val.toString())));
        configurer.configure(INSTRUMENTS_STORE_NAME, config -> config.editColumns(cols -> {
                    cols.remove(SCOPE_CONSTANT);
                    cols.remove(ASOFDATE);
                    return cols;
                })
                .columnCalculator(new ScopedColumnCalculator<>(
                        SCOPE_CONSTANT, (context, scope) -> scope.get(CONSTANT_SCOPE_PARAMETER)))
                .columnCalculator(asOfDateColumnCalculator()));
        configurer.configure(CPTY_STORE_NAME, config -> config.editColumns(cols -> {
                    cols.remove(ASOFDATE);
                    return cols;
                })
                // this could be done with a column calculator but wanted to show how to use tuple publishers!
                .tuplePublisherConfiguration(CsvStoresDataLoadConfigurer.tuplePublisherSupplierBuilder()
                        .accumulateTuples(false)
                        .creationFunction(
                                (datastore, s) -> new LambdaTuplePublisher<>(datastore, s, (message, rows) -> {
                                    rows.forEach(r -> r[0] = "CPTY " + r[0]);
                                    return rows;
                                }))
                        .build())
                .columnCalculator(asOfDateColumnCalculator()));
        return null;
    }
}
