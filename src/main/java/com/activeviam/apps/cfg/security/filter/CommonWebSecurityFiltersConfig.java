/*
 * Copyright (C) ActiveViam 2023-2024
 * ALL RIGHTS RESERVED. This material is the CONFIDENTIAL and PROPRIETARY
 * property of ActiveViam Limited. Any unauthorized use,
 * reproduction or transfer of this material is strictly prohibited
 */
package com.activeviam.apps.cfg.security.filter;

import static com.activeviam.apps.cfg.security.SecurityConstants.ROLE_ACTUATOR;
import static com.qfs.QfsWebUtils.url;
import static org.springframework.security.config.Customizer.withDefaults;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.h2.H2ConsoleProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;

import com.activeviam.apps.cfg.security.dsl.AuthenticationDslProvider;
import com.activeviam.spring.config.adminui.AdminUIResourceServerConfig;
import com.qfs.server.cfg.impl.JwtRestServiceConfig;
import com.qfs.server.cfg.impl.VersionServicesConfig;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class CommonWebSecurityFiltersConfig {
    public static final String WILDCARD = "**";

    private final AuthenticationDslProvider authenticationDslProvider;

    @Bean
    @Order(1)
    protected SecurityFilterChain actuatorFilterChain(HttpSecurity httpSecurity, MvcRequestMatcher.Builder mvc)
            throws Exception {
        return httpSecurity
                .with(authenticationDslProvider.basicAuth(), withDefaults())
                .securityMatcher(mvc.pattern(url("actuator", WILDCARD)))
                .authorizeHttpRequests(auth -> auth.anyRequest().hasAnyAuthority(ROLE_ACTUATOR))
                .build();
    }

    /**
     * Must be done before ActivePivotSecurityConfigurer / ContentServiceSecurityConfigurer (because they match common URLs)
     *
     * @param http allows to configure the security for HTTP requests
     * @return the filter
     * @throws Exception when the definition is not correct
     */
    @Bean
    @Order(2)
    protected SecurityFilterChain jwtFilterChain(HttpSecurity http, MvcRequestMatcher.Builder mvc) throws Exception {
        return http.with(authenticationDslProvider.nothing(), withDefaults())
                .securityMatcher(mvc.pattern(url(JwtRestServiceConfig.REST_API_URL_PREFIX, WILDCARD)))
                .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
                .build();
    }

    @Bean
    @Order(3)
    public SecurityFilterChain activePivotVersionFilterChain(HttpSecurity http, MvcRequestMatcher.Builder mvc)
            throws Exception {
        return http.securityMatcher(mvc.pattern(url(VersionServicesConfig.REST_API_URL_PREFIX, WILDCARD)))
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .build();
    }

    @ConditionalOnProperty(prefix = "spring.h2.console", name = "enabled", havingValue = "true")
    @Bean
    @Order(4)
    public SecurityFilterChain h2ConsoleSecurityFilterChain(
            HttpSecurity http, MvcRequestMatcher.Builder mvc, H2ConsoleProperties h2ConsoleProperties)
            throws Exception {
        return http.with(authenticationDslProvider.nothing(), withDefaults())
                .securityMatcher(mvc.servletPath(h2ConsoleProperties.getPath()).pattern(url(WILDCARD)))
                .headers(httpSecurityHeadersConfigurer ->
                        httpSecurityHeadersConfigurer.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin))
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .build();
    }

    @Bean
    @Order(5)
    public SecurityFilterChain adminUISecurityFilterChain(HttpSecurity http, MvcRequestMatcher.Builder mvc)
            throws Exception {
        return http.with(authenticationDslProvider.formLogin(), withDefaults())
                .securityMatcher(mvc.pattern(url(AdminUIResourceServerConfig.DEFAULT_NAMESPACE, WILDCARD)))
                .headers(httpSecurityHeadersConfigurer ->
                        httpSecurityHeadersConfigurer.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
                .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
                .build();
    }
}
