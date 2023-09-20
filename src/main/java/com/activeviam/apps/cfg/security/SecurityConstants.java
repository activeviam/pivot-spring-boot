/*
 * Copyright (C) ActiveViam 2023
 * ALL RIGHTS RESERVED. This material is the CONFIDENTIAL and PROPRIETARY
 * property of ActiveViam Limited. Any unauthorized use,
 * reproduction or transfer of this material is strictly prohibited
 */
package com.activeviam.apps.cfg.security;

import com.qfs.content.service.IContentService;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SecurityConstants {
    public static final String ROLE_PREFIX = "ROLE_";
    /**
     * Admin role
     */
    public static final String ROLE_ADMIN = "ROLE_ADMIN";

    public static final String ROLE_USER = "ROLE_USER";
    /**
     * Tech roles
     */
    public static final String ROLE_TECH = "ROLE_TECH";

    public static final String ROLE_ACTUATOR = "ROLE_ACTUATOR";

    /**
     * Content Server Root role
     */
    public static final String ROLE_CS_ROOT = IContentService.ROLE_ROOT;
}
