/*
 * Copyright (C) ActiveViam 2023
 * ALL RIGHTS RESERVED. This material is the CONFIDENTIAL and PROPRIETARY
 * property of ActiveViam Limited. Any unauthorized use,
 * reproduction or transfer of this material is strictly prohibited
 */
package com.activeviam.apps.cfg.security;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.qfs.jwt.impl.JwtAuthenticationProvider;
import com.qfs.jwt.impl.JwtUtil;
import com.qfs.server.cfg.impl.JwtConfig;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
@Primary
public class ApplicationJwtConfig extends JwtConfig {
    private final SecurityJwtProperties jwtProperties;

    @Bean
    @Override
    public JwtAuthenticationProvider jwtAuthenticationProvider() {
        var provider = super.jwtAuthenticationProvider();
        provider.setFailOnDifferentAuthorities(jwtProperties.isFailOnDifferentAuthorities());
        return provider;
    }

    @Override
    public RSAPublicKey getPublicKey() {
        return JwtUtil.parseRSAPublicKey(jwtProperties.getPublicKey());
    }

    @Override
    protected RSAPrivateKey getPrivateKey() {
        return JwtUtil.parseRSAPrivateKey(jwtProperties.getPrivateKey());
    }

    @Override
    protected int getExpiration() {
        return (int) jwtProperties.getExpiration().getSeconds();
    }
}
