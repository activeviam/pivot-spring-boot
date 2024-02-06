/*
 * Copyright (C) ActiveViam 2024
 * ALL RIGHTS RESERVED. This material is the CONFIDENTIAL and PROPRIETARY
 * property of ActiveViam Limited. Any unauthorized use,
 * reproduction or transfer of this material is strictly prohibited
 */
package com.activeviam.apps.cfg.security;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Duration;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

import com.qfs.jwt.impl.JwtUtil;
import com.qfs.jwt.service.impl.AJwtService;

import lombok.Data;

/**
 * @author ActiveViam
 */
@Data
public class JwtProperties {
    public static final Duration DEFAULT_EXPIRATION = Duration.ofHours(12);

    private boolean generate = true;
    private Duration expiration = DEFAULT_EXPIRATION;
    private boolean checkUserDetails;
    private boolean failOnDifferentAuthorities;

    @Valid
    private Key key = new Key();

    private ClaimKey claimKey = new ClaimKey();

    public RSAPublicKey getPublicKey() {
        return JwtUtil.parseRSAPublicKey(getKey().getPublicKey());
    }

    public RSAPrivateKey getPrivateKey() {
        return JwtUtil.parseRSAPrivateKey(getKey().getPrivateKey());
    }

    public String getAuthoritiesClaimKey() {
        return getClaimKey().getAuthorities();
    }

    public String getPrincipalClaimKey() {
        return getClaimKey().getPrincipal();
    }

    @Data
    public static class Key {
        @NotBlank
        private String publicKey;

        @NotBlank
        private String privateKey;
    }

    @Data
    public static class ClaimKey {
        private String authorities = AJwtService.AUTHORITIES_CLAIM_KEY;
        private String principal = AJwtService.PRINCIPAL_CLAIM_KEY;
    }
}
