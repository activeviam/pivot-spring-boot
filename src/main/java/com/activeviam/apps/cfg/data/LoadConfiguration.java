/*
 * Copyright (C) ActiveViam 2024
 * ALL RIGHTS RESERVED. This material is the CONFIDENTIAL and PROPRIETARY
 * property of ActiveViam Limited. Any unauthorized use,
 * reproduction or transfer of this material is strictly prohibited
 */

package com.activeviam.apps.cfg.data;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.activeviam.data.load.DataLoadConfiguration;
import com.activeviam.data.load.source.csv.CsvDatasourceConfiguration;

@Import({CsvDatasourceConfiguration.class, DataLoadConfiguration.class})
@Configuration
public class LoadConfiguration {}
