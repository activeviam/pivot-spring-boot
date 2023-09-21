/*
 * Copyright (C) ActiveViam 2023
 * ALL RIGHTS RESERVED. This material is the CONFIDENTIAL and PROPRIETARY
 * property of ActiveViam Limited. Any unauthorized use,
 * reproduction or transfer of this material is strictly prohibited
 */
package com.activeviam.apps.cfg.content;

import static com.activeviam.apps.cfg.content.ContentServiceProperties.CONTENT_SERVICE_PROPERTIES_PREFIX;
import static com.activeviam.apps.cfg.content.ContentServiceProperties.EMBEDDED_CONTENT_SERVER;

import java.io.Serial;
import java.util.Properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author ActiveViam
 */
@ConfigurationProperties(EmbeddedContentServerHibernateProperties.EMBEDDED_CONTENT_SERVER_HIBERNATE_PROPERTIES)
public class EmbeddedContentServerHibernateProperties extends Properties {
    public static final String EMBEDDED_CONTENT_SERVER_HIBERNATE_PROPERTIES =
            CONTENT_SERVICE_PROPERTIES_PREFIX + "." + EMBEDDED_CONTENT_SERVER;

    @Serial
    private static final long serialVersionUID = -2772962974049022623L;
}
