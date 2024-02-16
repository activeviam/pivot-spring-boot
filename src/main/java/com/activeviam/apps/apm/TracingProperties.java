/*
 * Copyright (C) ActiveViam 2023-2024
 * ALL RIGHTS RESERVED. This material is the CONFIDENTIAL and PROPRIETARY
 * property of ActiveViam Limited. Any unauthorized use,
 * reproduction or transfer of this material is strictly prohibited
 */
package com.activeviam.apps.apm;

import org.springframework.boot.context.properties.ConfigurationProperties;

import com.activeviam.tracing.impl.SpanLevel;

import lombok.Data;

@ConfigurationProperties("tracing")
@Data
public class TracingProperties {
    private SpanLevel spanLevel = SpanLevel.SPECIFIC;
}
