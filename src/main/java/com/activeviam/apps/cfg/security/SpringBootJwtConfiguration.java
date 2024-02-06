/*
 * Copyright (C) ActiveViam 2024
 * ALL RIGHTS RESERVED. This material is the CONFIDENTIAL and PROPRIETARY
 * property of ActiveViam Limited. Any unauthorized use,
 * reproduction or transfer of this material is strictly prohibited
 */
package com.activeviam.apps.cfg.security;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.validation.annotation.Validated;

import com.qfs.jwt.impl.JwtAuthenticationProvider;
import com.qfs.jwt.service.IJwtService;
import com.qfs.jwt.service.impl.JwtService;
import com.quartetfs.biz.pivot.security.IAuthorityComparator;

/**
 * @author ActiveViam
 */
@Configuration
public class SpringBootJwtConfiguration {
    public static final String JWT_SECURITY_PROPERTIES = "activeviam.jwt";

    @Bean
    @ConfigurationProperties(prefix = JWT_SECURITY_PROPERTIES)
    @Validated
    public JwtProperties jwtProperties() {
        return new JwtProperties();
    }

    @Bean
    public IJwtService jwtService(IAuthorityComparator authorityComparator, JwtProperties jwtProperties) {
        return jwtProperties.isGenerate()
                ? new JwtService(authorityComparator, jwtProperties.getPrivateKey(), (int)
                        jwtProperties.getExpiration().toSeconds())
                : null;
    }

    @Bean
    public JwtAuthenticationProvider jwtAuthenticationProvider(
            UserDetailsService userDetailsService, JwtProperties jwtProperties) {
        var provider = new JwtAuthenticationProvider(
                jwtProperties.getPublicKey(),
                jwtProperties.isCheckUserDetails() ? userDetailsService : null,
                jwtProperties.getPrincipalClaimKey(),
                jwtProperties.getAuthoritiesClaimKey());
        provider.setFailOnDifferentAuthorities(jwtProperties.isFailOnDifferentAuthorities());
        return provider;
    }

    @Bean
    public SpringBootJwtFilter jwtFilter(ApplicationContext applicationContext, JwtProperties jwtProperties) {
        return new SpringBootJwtFilter(
                applicationContext, jwtProperties.getPrincipalClaimKey(), jwtProperties.getAuthoritiesClaimKey());
    }
}
