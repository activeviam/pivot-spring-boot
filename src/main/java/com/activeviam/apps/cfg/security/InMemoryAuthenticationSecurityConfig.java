/*
 * Copyright (C) ActiveViam 2023
 * ALL RIGHTS RESERVED. This material is the CONFIDENTIAL and PROPRIETARY
 * property of ActiveViam Limited. Any unauthorized use,
 * reproduction or transfer of this material is strictly prohibited
 */
package com.activeviam.apps.cfg.security;

import static com.activeviam.apps.cfg.security.SecurityConstants.ROLE_ADMIN;
import static com.activeviam.apps.cfg.security.SecurityConstants.ROLE_CS_ROOT;
import static com.activeviam.apps.cfg.security.SecurityConstants.ROLE_USER;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class InMemoryAuthenticationSecurityConfig {
    private static final String ADMIN = "admin";
    private static final String USER = "user";

    private final PasswordEncoder passwordEncoder;

    @Bean
    public AuthenticationProvider inMemoryAuthenticationProvider() {
        var authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setPasswordEncoder(passwordEncoder);
        authenticationProvider.setUserDetailsService(inMemoryUserDetailsService());
        return authenticationProvider;
    }

    @Bean
    public UserDetailsManager inMemoryUserDetailsService() {
        var builder = new InMemoryUserDetailsManagerBuilder();
        builder.passwordEncoder(passwordEncoder)
                // Default user for ActivePivot server
                .withUser(ADMIN)
                .password(passwordEncoder.encode(ADMIN))
                .authorities(ROLE_ADMIN, ROLE_USER, ROLE_CS_ROOT)
                .and()
                .withUser(USER)
                .password(passwordEncoder.encode(USER))
                .authorities(ROLE_USER);
        return builder.build();
    }
}
