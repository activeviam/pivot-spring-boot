/*
 * Copyright (C) ActiveViam 2024
 * ALL RIGHTS RESERVED. This material is the CONFIDENTIAL and PROPRIETARY
 * property of ActiveViam Limited. Any unauthorized use,
 * reproduction or transfer of this material is strictly prohibited
 */
package com.activeviam.apps.cfg.security.dsl.local;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.AuthenticationEntryPoint;

import com.activeviam.apps.cfg.security.SpringBootContextValueFilter;
import com.activeviam.apps.cfg.security.SpringBootJwtFilter;
import com.activeviam.apps.cfg.security.dsl.AActivePivotAuthenticationDsl;

/**
 * @author ActiveViam
 */
public class BasicAuthActivePivotAuthenticationDsl extends AActivePivotAuthenticationDsl {
    private final AuthenticationEntryPoint authenticationEntryPoint;

    public BasicAuthActivePivotAuthenticationDsl(
            SpringBootJwtFilter jwtFilter,
            SpringBootContextValueFilter contextValueFilter,
            AuthenticationEntryPoint authenticationEntryPoint) {
        super(jwtFilter, contextValueFilter);
        this.authenticationEntryPoint = authenticationEntryPoint;
    }

    @Override
    public void addAuthentication(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.httpBasic(customizer -> customizer.authenticationEntryPoint(authenticationEntryPoint));
    }
}
