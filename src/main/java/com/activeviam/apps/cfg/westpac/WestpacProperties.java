/*
 * Copyright (C) ActiveViam 2023
 * ALL RIGHTS RESERVED. This material is the CONFIDENTIAL and PROPRIETARY
 * property of ActiveViam Limited. Any unauthorized use,
 * reproduction or transfer of this material is strictly prohibited
 */
package com.activeviam.apps.cfg.westpac;

import java.util.Properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

/**
 * @author ActiveViam
 */
@ConfigurationProperties(WestpacProperties.WESTPAC_PROPERTIES_PREFIX)
@Data
public class WestpacProperties {
    public static final String WESTPAC_PROPERTIES_PREFIX = "westpac";

    private Jdbc jdbc = new Jdbc();

    @Data
    public static final class Jdbc {
        private String driver;
        private String url;
        private String query;
        private Properties properties = new Properties();
    }
}
