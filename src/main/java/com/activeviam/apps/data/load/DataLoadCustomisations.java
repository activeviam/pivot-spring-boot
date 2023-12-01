/*
 * Copyright (C) ActiveViam 2023
 * ALL RIGHTS RESERVED. This material is the CONFIDENTIAL and PROPRIETARY
 * property of ActiveViam Limited. Any unauthorized use,
 * reproduction or transfer of this material is strictly prohibited
 */

package com.activeviam.apps.data.load;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.activeviam.data.load.DataLoadConfiguration;
import com.activeviam.data.load.source.csv.CsvDatasourceConfiguration;
import com.activeviam.data.load.source.csv.model.CsvStoresDataLoadConfigurer;

@Configuration
@Import({DataLoadConfiguration.class, CsvDatasourceConfiguration.class})
public class DataLoadCustomisations {

    @Bean
    Void csvDataLoadCustomisations(CsvStoresDataLoadConfigurer configurer){
        return null;
    }
}
