package com.activeviam.apps.cfg.security;

import com.qfs.jwt.impl.JwtAuthenticationProvider;
import com.qfs.jwt.impl.JwtUtil;
import com.qfs.server.cfg.impl.JwtConfig;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

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