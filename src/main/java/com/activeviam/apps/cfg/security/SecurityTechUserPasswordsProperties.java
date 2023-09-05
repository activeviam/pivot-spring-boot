/*
 * Copyright (C) ActiveViam 2023
 * ALL RIGHTS RESERVED. This material is the CONFIDENTIAL and PROPRIETARY
 * property of ActiveViam Limited. Any unauthorized use,
 * reproduction or transfer of this material is strictly prohibited
 */
package com.activeviam.apps.cfg.security;

import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;

import lombok.Data;

@ConfigurationProperties(prefix = SecurityTechUserPasswordsProperties.TECH_PROPERTIES_PREFIX)
@Data
public class SecurityTechUserPasswordsProperties {
    public static final String TECH_PROPERTIES_PREFIX = "tech-user.passwords";
    private static final DelegatingPasswordEncoder ENCODER =
            new DelegatingPasswordEncoder("bcrypt", Map.of("bcrypt", new BCryptPasswordEncoder()));

    /**
     * Password of technical user for AP Server.
     */
    private String pivot = ENCODER.encode(TechnicalAuthenticationSecurityConfig.PIVOT_TECH_USER_LOGIN);
    /**
     * Password of technical user for Spring Boot Admin.
     */
    private String sba = ENCODER.encode("s3cret");
}
