/*
 * Copyright (C) ActiveViam 2023
 * ALL RIGHTS RESERVED. This material is the CONFIDENTIAL and PROPRIETARY
 * property of ActiveViam Limited. Any unauthorized use,
 * reproduction or transfer of this material is strictly prohibited
 */
package com.activeviam.apps.cfg.security;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import com.activeviam.security.cfg.ICorsConfig;

@Configuration
public class CorsConfig implements ICorsConfig {
    @Override
    public List<String> getAllowedOrigins() {
        return Collections.singletonList(CorsConfiguration.ALL);
    }

    /**
     * [Bean] Spring standard way of configuring CORS.
     *
     * <p>This simply forwards the configuration of {@link ICorsConfig} to Spring security system.
     *
     * @return the configuration for the application.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        var configuration = new CorsConfiguration();
        var allowedHeader = new ArrayList<>(getAllowedHeaders());
        allowedHeader.add(HttpHeaders.REFERER);
        allowedHeader.add("Referrer-Policy");
        configuration.setAllowedHeaders(allowedHeader);
        configuration.setExposedHeaders(getExposedHeaders());
        configuration.setAllowedMethods(getAllowedMethods());
        configuration.setAllowedOriginPatterns(getAllowedOrigins());
        configuration.setAllowCredentials(true);
        var source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    /**
     * [Bean] Configuration to give precedence to CORS filter
     * in order to accept preflight requests.
     *
     * @return The filter wrapping the CORS configuration
     */
    @Bean
    public FilterRegistrationBean<CorsFilter> customCorsFilter() {
        var bean = new FilterRegistrationBean<>(new CorsFilter(corsConfigurationSource()));
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return bean;
    }
}
