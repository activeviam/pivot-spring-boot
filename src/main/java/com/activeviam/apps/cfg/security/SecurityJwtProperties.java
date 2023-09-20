/*
 * Copyright (C) ActiveViam 2023
 * ALL RIGHTS RESERVED. This material is the CONFIDENTIAL and PROPRIETARY
 * property of ActiveViam Limited. Any unauthorized use,
 * reproduction or transfer of this material is strictly prohibited
 */
package com.activeviam.apps.cfg.security;

import java.time.Duration;
import java.util.Map;

import javax.validation.constraints.NotNull;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;

@ConfigurationProperties(SecurityJwtProperties.JWT_PROPERTIES_PREFIX)
@Data
@Validated
public class SecurityJwtProperties {
    public static final String JWT_PROPERTIES_PREFIX = "activeviam.jwt";
    private static final String PUBLIC_KEY = "public";
    private static final String PRIVATE_KEY = "private";

    @NotNull
    @Getter(AccessLevel.PRIVATE)
    private Map<String, String> key;

    private Duration expiration = Duration.ofHours(12);
    private boolean configureLogout = true;
    private boolean failOnDifferentAuthorities;

    public String getPublicKey() {
        return key.get(PUBLIC_KEY);
    }

    public String getPrivateKey() {
        return key.get(PRIVATE_KEY);
    }
}
