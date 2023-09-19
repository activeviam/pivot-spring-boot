/*
 * Copyright (C) ActiveViam 2023
 * ALL RIGHTS RESERVED. This material is the CONFIDENTIAL and PROPRIETARY
 * property of ActiveViam Limited. Any unauthorized use,
 * reproduction or transfer of this material is strictly prohibited
 */
package com.activeviam.apps.cfg.security;

import javax.validation.constraints.NotNull;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import lombok.Data;

@ConfigurationProperties(prefix = SecurityTechUserPasswordsProperties.TECH_PROPERTIES_PREFIX)
@Data
@Validated
public class SecurityTechUserPasswordsProperties {
    public static final String TECH_PROPERTIES_PREFIX = "tech-user.passwords";

    /**
     * Password of technical user for AP Server.
     */
    @NotNull
    private String pivot;
    /**
     * Password of technical user for Spring Boot Admin.
     */
    @NotNull
    private String sba;
}
