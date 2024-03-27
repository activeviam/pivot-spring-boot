/*
 * Copyright (C) ActiveViam 2024
 * ALL RIGHTS RESERVED. This material is the CONFIDENTIAL and PROPRIETARY
 * property of ActiveViam Limited. Any unauthorized use,
 * reproduction or transfer of this material is strictly prohibited
 */

package com.activeviam.apps.cfg;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.activeviam.apm.cfg.impl.ExtraLoggingConfig;
import com.activeviam.apm.cfg.impl.LoggingConfig;
import com.activeviam.apm.cfg.impl.MonitoredDataLoadingConfig;
import com.activeviam.apm.cfg.impl.MonitoringJmxConfig;
import com.activeviam.apm.cfg.impl.QueryPerformanceEvaluatorConfig;

@Configuration
@Import(
// APM
// (https://docs.activeviam.com/products/atoti/server/latest/docs/monitoring/application_performance_monitoring/#monitored-spring-configuration)
{
    LoggingConfig.class,
    MonitoringJmxConfig.class,
    MonitoredDataLoadingConfig.class,
    QueryPerformanceEvaluatorConfig.class,
    ExtraLoggingConfig.class
})
public class TracingConfiguration {}
