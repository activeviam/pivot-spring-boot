/*
 * Copyright (C) ActiveViam 2023
 * ALL RIGHTS RESERVED. This material is the CONFIDENTIAL and PROPRIETARY
 * property of ActiveViam Limited. Any unauthorized use,
 * reproduction or transfer of this material is strictly prohibited
 */
package com.activeviam.apps.cfg.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;

import com.qfs.content.service.IContentService;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class TechnicalAuthenticationSecurityConfig {
    public static final String PIVOT_TECH_USER_LOGIN = "pivot";
    public static final String SBA_TECH_USER_LOGIN = "sba";

    private final PasswordEncoder passwordEncoder;
    private final SecurityTechUserPasswordsProperties techUserPasswordsProperties;

    @Bean
    public AuthenticationProvider technicalAuthenticationProvider() {
        var authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setPasswordEncoder(passwordEncoder);
        authenticationProvider.setUserDetailsService(technicalUserDetailsService());
        return authenticationProvider;
    }

    @Bean
    public UserDetailsManager technicalUserDetailsService() {
        var builder = new InMemoryUserDetailsManagerBuilder();
        // Technical user for ActivePivot server
        builder.passwordEncoder(passwordEncoder);
        builder.withUser(PIVOT_TECH_USER_LOGIN)
                .password(techUserPasswordsProperties.getPivot())
                .authorities(SecurityConstants.ROLE_TECH, IContentService.ROLE_ROOT);
        builder.withUser(SBA_TECH_USER_LOGIN)
                .password(techUserPasswordsProperties.getSba())
                .authorities(SecurityConstants.ROLE_ACTUATOR);
        return builder.build();
    }
}
