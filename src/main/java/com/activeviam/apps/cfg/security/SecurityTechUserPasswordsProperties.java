/*
 * Copyright (C) ActiveViam 2023
 * ALL RIGHTS RESERVED. This material is the CONFIDENTIAL and PROPRIETARY
 * property of ActiveViam Limited. Any unauthorized use,
 * reproduction or transfer of this material is strictly prohibited
 */
package com.activeviam.apps.cfg.security;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = SecurityTechUserPasswordsProperties.TECH_PROPERTIES_PREFIX)
@Data
public class SecurityTechUserPasswordsProperties {
    public static final String TECH_PROPERTIES_PREFIX = "tech-user.passwords";

    /**
     * Password of technical user for AP Server.
     */
    private String pivot;
    /**
     * Password of technical user for Spring Boot Admin.
     */
    private String sba;
}
