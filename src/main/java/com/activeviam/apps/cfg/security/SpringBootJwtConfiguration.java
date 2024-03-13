/*
 * Copyright (C) ActiveViam 2024
 * ALL RIGHTS RESERVED. This material is the CONFIDENTIAL and PROPRIETARY
 * property of ActiveViam Limited. Any unauthorized use,
 * reproduction or transfer of this material is strictly prohibited
 */
package com.activeviam.apps.cfg.security;

import java.util.Collection;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.validation.annotation.Validated;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.jca.JCAContext;
import com.nimbusds.jose.util.Base64URL;
import com.qfs.jwt.impl.JwtAuthenticationProvider;
import com.qfs.jwt.service.IJwtService;
import com.qfs.jwt.service.impl.ASignedJwtService;
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
                //                ? new JwtService(authorityComparator, jwtProperties.getPrivateKey(), (int)
                //                        jwtProperties.getExpiration().toSeconds())
                ? new ASignedJwtService(
                        authorityComparator,
                        new JWSSigner() {
                            @Override
                            public Base64URL sign(JWSHeader header, byte[] signingInput) {
                                return new Base64URL(StringUtils.EMPTY);
                            }

                            @Override
                            public Set<JWSAlgorithm> supportedJWSAlgorithms() {
                                return Set.of(JWSAlgorithm.RS512);
                            }

                            @Override
                            public JCAContext getJCAContext() {
                                return null;
                            }
                        },
                        JwtService.DEFAULT_ISSUER,
                        (int) jwtProperties.getExpiration().toSeconds()) {
                    @Override
                    public String getToken(String username, Collection<String> authorities) {
                        var token = super.getToken(username, authorities);
                        return token.substring(0, token.lastIndexOf('.'));
                    }
                }
                : null;
    }

    @Bean
    public JwtAuthenticationProvider jwtAuthenticationProvider(
            UserDetailsService userDetailsService, JwtProperties jwtProperties) {
        return null;
        //        var provider = new JwtAuthenticationProvider(
        //                jwtProperties.getPublicKey(),
        //                jwtProperties.isCheckUserDetails() ? userDetailsService : null,
        //                jwtProperties.getPrincipalClaimKey(),
        //                jwtProperties.getAuthoritiesClaimKey());
        //        provider.setFailOnDifferentAuthorities(jwtProperties.isFailOnDifferentAuthorities());
        //        return provider;
    }

    @Bean
    public SpringBootJwtFilter jwtFilter(ApplicationContext applicationContext, JwtProperties jwtProperties) {
        return new SpringBootJwtFilter(
                applicationContext, jwtProperties.getPrincipalClaimKey(), jwtProperties.getAuthoritiesClaimKey());
    }
}
