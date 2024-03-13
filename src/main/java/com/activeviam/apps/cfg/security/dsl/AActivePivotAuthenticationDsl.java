/*
 * Copyright (C) ActiveViam 2024
 * ALL RIGHTS RESERVED. This material is the CONFIDENTIAL and PROPRIETARY
 * property of ActiveViam Limited. Any unauthorized use,
 * reproduction or transfer of this material is strictly prohibited
 */
package com.activeviam.apps.cfg.security.dsl;

import java.util.Optional;

import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.authentication.switchuser.SwitchUserFilter;

import com.activeviam.apps.cfg.security.SpringBootContextValueFilter;
import com.activeviam.apps.cfg.security.SpringBootJwtFilter;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

/**
 * @author ActiveViam
 */
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AActivePivotAuthenticationDsl
        extends AbstractHttpConfigurer<AActivePivotAuthenticationDsl, HttpSecurity> {
    private final SpringBootJwtFilter jwtFilter;
    private final SpringBootContextValueFilter contextValueFilter;

    public abstract void addAuthentication(HttpSecurity httpSecurity) throws Exception;

    @Override
    public void init(HttpSecurity builder) throws Exception {
        super.init(builder);
        // any method that adds another configurer
        // must be done in the init method
        builder
                // As of Spring Security 4.0, CSRF protection is enabled by default.
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults());
        addAuthentication(builder);
    }

    @Override
    public void configure(HttpSecurity builder) throws Exception {
        super.configure(builder);
        Optional.ofNullable(contextValueFilter).ifPresent(f -> builder.addFilterAfter(f, SwitchUserFilter.class));
        // To Allow authentication with JWT ( Needed for Active UI )
        // Optional.ofNullable(jwtFilter).ifPresent(f -> builder.addFilterAfter(f, CorsFilter.class));
    }
}
