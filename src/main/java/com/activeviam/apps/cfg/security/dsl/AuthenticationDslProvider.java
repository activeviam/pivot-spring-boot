/*
 * Copyright (C) ActiveViam 2024
 * ALL RIGHTS RESERVED. This material is the CONFIDENTIAL and PROPRIETARY
 * property of ActiveViam Limited. Any unauthorized use,
 * reproduction or transfer of this material is strictly prohibited
 */
package com.activeviam.apps.cfg.security.dsl;

public record AuthenticationDslProvider(
        AActivePivotAuthenticationDsl nothing,
        AActivePivotAuthenticationDsl formLogin,
        AActivePivotAuthenticationDsl basicAuth) {}
