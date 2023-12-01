/*
 * Copyright (C) ActiveViam 2023
 * ALL RIGHTS RESERVED. This material is the CONFIDENTIAL and PROPRIETARY
 * property of ActiveViam Limited. Any unauthorized use,
 * reproduction or transfer of this material is strictly prohibited
 */

package com.activeviam.apps.data.load;

import static com.activeviam.apps.constants.StoreAndFieldConstants.INSTRUMENTS_STORE_NAME;
import static com.activeviam.apps.constants.StoreAndFieldConstants.SCOPE_CONSTANT;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.activeviam.data.load.DataLoadConfiguration;
import com.activeviam.data.load.ScopedColumnCalculator;
import com.activeviam.data.load.source.csv.CsvDatasourceConfiguration;
import com.activeviam.data.load.source.csv.model.CsvStoresDataLoadConfigurer;

@Configuration
@Import({DataLoadConfiguration.class, CsvDatasourceConfiguration.class})
public class DataLoadCustomisations {

    public static final String CONSTANT_SCOPE_PARAMETER = "CONSTANT";

    @Bean
    Void csvDataLoadCustomisations(CsvStoresDataLoadConfigurer configurer) {
        configurer.configure(INSTRUMENTS_STORE_NAME, config -> config.editColumns(cols -> {
                    cols.remove(SCOPE_CONSTANT);
                    return cols;
                })
                .columnCalculator(new ScopedColumnCalculator<>(
                        SCOPE_CONSTANT,
                        (context, scope) -> scope.hasParameter(CONSTANT_SCOPE_PARAMETER)
                                ? scope.get(CONSTANT_SCOPE_PARAMETER)
                                : context.getValue(SCOPE_CONSTANT))));
        return null;
    }
}
