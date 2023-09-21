/*
 * Copyright (C) ActiveViam 2023
 * ALL RIGHTS RESERVED. This material is the CONFIDENTIAL and PROPRIETARY
 * property of ActiveViam Limited. Any unauthorized use,
 * reproduction or transfer of this material is strictly prohibited
 */
package com.activeviam.apps.cfg.content;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

/**
 * @author ActiveViam
 */
@ConfigurationProperties(ContentServiceProperties.CONTENT_SERVICE_PROPERTIES_PREFIX)
@Data
public class ContentServiceProperties {
    public static final String CONTENT_SERVICE_PROPERTIES_PREFIX = "content-service";
    public static final String CONTENT_SERVER_PROPERTY = "server";
    public static final String EMBEDDED_CONTENT_SERVER = "embedded";
    public static final String CONTENT_SERVICE_SECURITY_PROPERTIES = "security";

    private String server = EMBEDDED_CONTENT_SERVER;
}
