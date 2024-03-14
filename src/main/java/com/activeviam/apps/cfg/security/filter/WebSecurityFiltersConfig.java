/*
 * Copyright (C) ActiveViam 2023-2024
 * ALL RIGHTS RESERVED. This material is the CONFIDENTIAL and PROPRIETARY
 * property of ActiveViam Limited. Any unauthorized use,
 * reproduction or transfer of this material is strictly prohibited
 */
package com.activeviam.apps.cfg.security.filter;

import static com.activeviam.apps.cfg.security.SecurityConstants.ROLE_ADMIN;
import static com.activeviam.apps.cfg.security.SecurityConstants.ROLE_TECH;
import static com.activeviam.apps.cfg.security.SecurityConstants.ROLE_USER;
import static com.activeviam.apps.cfg.security.filter.CommonWebSecurityFiltersConfig.WILDCARD;
import static com.qfs.QfsWebUtils.url;
import static com.qfs.server.cfg.impl.ActivePivotRestServicesConfig.PING_SUFFIX;
import static com.qfs.server.cfg.impl.ActivePivotRestServicesConfig.REST_API_URL_PREFIX;
import static com.qfs.server.cfg.impl.ActivePivotWebSocketServicesConfig.WEB_SOCKET_ENDPOINT;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

import com.activeviam.apps.cfg.security.JwtAuthenticationConfigurer;
import com.activeviam.spring.config.activeui.ActiveUIResourceServerConfig;
import com.qfs.content.cfg.impl.ContentServerRestServicesConfig;
import com.qfs.content.rest.impl.ARestContentServer;
import com.qfs.service.store.IDatabaseRestService;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityFiltersConfig {
    private final JwtAuthenticationConfigurer jwtAuthenticationConfigurer;

    @Scope("prototype")
    @Bean
    MvcRequestMatcher.Builder mvc(HandlerMappingIntrospector introspector) {
        return new MvcRequestMatcher.Builder(introspector);
    }

    @Bean
    @Order(4)
    public SecurityFilterChain embeddedCSFilterChain(HttpSecurity http, MvcRequestMatcher.Builder mvc)
            throws Exception {
        http
                // Only these URLs must be handled by this HttpSecurity
                .securityMatcher(mvc.pattern(url(ARestContentServer.CONTENT_NAMESPACE, WILDCARD)))
                .authorizeHttpRequests(auth -> auth
                        // The order of the matchers matters
                        .requestMatchers(mvc.pattern(
                                HttpMethod.OPTIONS, url(ContentServerRestServicesConfig.REST_API_URL_PREFIX, WILDCARD)))
                        .permitAll()
                        .anyRequest()
                        .hasAnyAuthority(ROLE_USER))
                .apply(jwtAuthenticationConfigurer);
        return http.build();
    }

    @Bean
    @Order(6)
    public SecurityFilterChain xmlaFilterChain(HttpSecurity http, MvcRequestMatcher.Builder mvc) throws Exception {
        return http.csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .securityMatcher(mvc.pattern(url("xmla", WILDCARD)))
                .authorizeHttpRequests(auth -> auth.requestMatchers(mvc.pattern(HttpMethod.POST, url(WILDCARD)))
                        .authenticated())
                .httpBasic(Customizer.withDefaults())
                .build();
    }

    @Bean
    @Order(20)
    public SecurityFilterChain activeUIFilterChain(HttpSecurity http) throws Exception {
        return http.securityMatcher(url("/"), url(ActiveUIResourceServerConfig.DEFAULT_NAMESPACE, WILDCARD))
                .headers(httpSecurityHeadersConfigurer ->
                        httpSecurityHeadersConfigurer.frameOptions().disable())
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .build();
    }

    @Bean
    @Order(99)
    public SecurityFilterChain coreActivePivotFilterChain(HttpSecurity http, MvcRequestMatcher.Builder mvc)
            throws Exception {
        http.authorizeHttpRequests(auth -> {
                    // Allow OPTIONS requests
                    auth.requestMatchers(mvc.pattern(HttpMethod.OPTIONS, url(REST_API_URL_PREFIX, WILDCARD)))
                            .permitAll();

                    // The ping service is temporarily authenticated (see PIVOT-3149)
                    auth.requestMatchers(mvc.pattern(url(REST_API_URL_PREFIX, PING_SUFFIX)))
                            .hasAnyAuthority(ROLE_USER, ROLE_TECH);

                    // pivot websocket
                    auth.requestMatchers(mvc.pattern(url(WEB_SOCKET_ENDPOINT, WILDCARD)))
                            .hasAnyAuthority(ROLE_USER);

                    // datastore rest service
                    auth.requestMatchers(mvc.pattern(url(IDatabaseRestService.DATABASE_API_URL_PREFIX, WILDCARD)))
                            .hasAnyAuthority(ROLE_USER);

                    // No existing constant for cube in the core
                    auth.requestMatchers(mvc.pattern(url(REST_API_URL_PREFIX, "cube", WILDCARD)))
                            .hasAnyAuthority(ROLE_USER);

                    // One has to be an admin for all the other URLs
                    auth.requestMatchers(mvc.pattern(url(WILDCARD))).hasAnyAuthority(ROLE_ADMIN);
                })
                .apply(jwtAuthenticationConfigurer);
        return http.build();
    }
}
