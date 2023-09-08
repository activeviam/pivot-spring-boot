/*
 * Copyright (C) ActiveViam 2023
 * ALL RIGHTS RESERVED. This material is the CONFIDENTIAL and PROPRIETARY
 * property of ActiveViam Limited. Any unauthorized use,
 * reproduction or transfer of this material is strictly prohibited
 */

package com.activeviam.apps.cfg.security.filter;

import static com.activeviam.apps.cfg.security.SecurityConstants.ROLE_ACTUATOR;
import static com.activeviam.apps.cfg.security.SecurityConstants.ROLE_ADMIN;
import static com.qfs.QfsWebUtils.url;

import com.activeviam.apps.cfg.security.JwtAuthenticationConfigurer;
import com.activeviam.apps.cfg.security.SecurityConstants;
import com.activeviam.spring.config.adminui.AdminUIResourceServerConfig;
import com.qfs.server.cfg.impl.JwtRestServiceConfig;
import com.qfs.server.cfg.impl.VersionServicesConfig;
import lombok.RequiredArgsConstructor;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.h2.H2ConsoleProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;

@Configuration
@RequiredArgsConstructor
public class CommonWebSecurityFiltersConfig {
    public static final String WILDCARD = "**";

    @Bean
    @Order(1)
    protected SecurityFilterChain actuatorFilterChain(HttpSecurity httpSecurity, MvcRequestMatcher.Builder mvc)
            throws Exception {
        return httpSecurity
                // As of Spring Security 4.0, CSRF protection is enabled by default.
                .csrf(AbstractHttpConfigurer::disable)
                // Configure CORS
                .cors(Customizer.withDefaults())
                .securityMatcher(mvc.servletPath("actuator").pattern(url(WILDCARD)))
                .authorizeHttpRequests(auth -> auth.anyRequest().hasAnyAuthority(ROLE_ACTUATOR))
                .httpBasic(Customizer.withDefaults())
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
        return http
                // As of Spring Security 4.0, CSRF protection is enabled by default.
                .csrf(AbstractHttpConfigurer::disable)
                // Configure CORS
                .cors(Customizer.withDefaults())
                .securityMatcher(mvc.pattern(url(JwtRestServiceConfig.REST_API_URL_PREFIX, WILDCARD)))
                .authorizeHttpRequests(auth -> auth.requestMatchers(mvc.pattern(HttpMethod.OPTIONS, url(WILDCARD)))
                        .permitAll()
                        .anyRequest()
                        .hasAnyAuthority(SecurityConstants.ROLE_USER))
                .httpBasic(basic -> basic.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
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
    @Order(7)
    public SecurityFilterChain h2ConsoleSecurityFilterChain(
            HttpSecurity http, MvcRequestMatcher.Builder mvc, H2ConsoleProperties h2ConsoleProperties)
            throws Exception {
        return http
                // As of Spring Security 4.0, CSRF protection is enabled by default.
                .csrf(AbstractHttpConfigurer::disable)
                // Configure CORS
                .cors(Customizer.withDefaults())
                .securityMatcher(mvc.servletPath(h2ConsoleProperties.getPath()).pattern(url(WILDCARD)))
                .headers(httpSecurityHeadersConfigurer ->
                        httpSecurityHeadersConfigurer.frameOptions().sameOrigin())
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .build();
    }

    @Bean
    @Order(8)
    public SecurityFilterChain adminUISecurityFilterChain(HttpSecurity http, MvcRequestMatcher.Builder mvc)
            throws Exception {
        return http.securityMatcher(mvc.pattern(url(AdminUIResourceServerConfig.DEFAULT_NAMESPACE, WILDCARD)))
                .headers(httpSecurityHeadersConfigurer ->
                        httpSecurityHeadersConfigurer.frameOptions().disable())
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .build();
    }
}
