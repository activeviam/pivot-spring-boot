/*
 * Copyright (C) ActiveViam 2023
 * ALL RIGHTS RESERVED. This material is the CONFIDENTIAL and PROPRIETARY
 * property of ActiveViam Limited. Any unauthorized use,
 * reproduction or transfer of this material is strictly prohibited
 */
package com.activeviam.apps.cfg.content;

import static com.activeviam.apps.cfg.content.ContentServiceProperties.CONTENT_SERVICE_PROPERTIES_PREFIX;
import static com.activeviam.apps.cfg.content.ContentServiceProperties.CONTENT_SERVICE_SECURITY_PROPERTIES;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;

import com.activeviam.apps.cfg.security.SecurityConstants;

import lombok.Data;

/**
 * @author ActiveViam
 */
@ConfigurationProperties(CONTENT_SERVICE_PROPERTIES_PREFIX + "." + CONTENT_SERVICE_SECURITY_PROPERTIES)
@Data
public class ContentServiceSecurityProperties {
    private String calculatedMemberRole = SecurityConstants.ROLE_USER;
    private String kpiRole = SecurityConstants.ROLE_USER;
    private Duration cacheEntitlementsTtl = Duration.ofHours(1);
}
