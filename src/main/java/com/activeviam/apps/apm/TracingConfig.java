/*
 * Copyright (C) ActiveViam 2023
 * ALL RIGHTS RESERVED. This material is the CONFIDENTIAL and PROPRIETARY
 * property of ActiveViam Limited. Any unauthorized use,
 * reproduction or transfer of this material is strictly prohibited
 */
package com.activeviam.apps.apm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.activeviam.tracing.impl.Tracing;
import com.activeviam.tracing.tracer.impl.DelegatingTracer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Configuration class to initiate the necessary tracing components.<br/>
 * It replaces {@link com.activeviam.pivot.tracing.TracingConfig} which is not Spring Boot friendly enough.
 *
 * @author ActiveViam
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class TracingConfig {
    private final TracingProperties tracingProperties;

    /**
     * Tracing configuration Bean.
     *
     * @return the initialized tracing
     */
    @Bean(name = com.activeviam.pivot.tracing.TracingConfig.TRACING_BEAN)
    public Void initTracing(@Autowired(required = false) Tracer tracer) {
        log.info("Initialisation of APM Tracing");
        Tracing.setSpanLevel(tracingProperties.getSpanLevel().name());
        if (tracer != null) {
            Tracing.trySetTracer(new DelegatingTracer(tracer));
        }
        return null;
    }
}
