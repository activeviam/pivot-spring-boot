/*
 * Copyright (C) ActiveViam 2023
 * ALL RIGHTS RESERVED. This material is the CONFIDENTIAL and PROPRIETARY
 * property of ActiveViam Limited. Any unauthorized use,
 * reproduction or transfer of this material is strictly prohibited
 */
package com.activeviam.apps.cfg.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class PasswordEncoderConfig {
    /**
     * As of Spring Security 5.0, the way the passwords are encoded must
     * be specified. When logging, the input password will be encoded
     * and compared with the stored encoded password. To determine which
     * encoding function was used to encode the password, the stored
     * encoded passwords are prefixed with the id of the encoding function.
     * <p>
     * In order to avoid reformatting existing passwords in databases one can
     * set the default <code>PasswordEncoder</code> to use for stored
     * passwords that are not prefixed. This is the role of the following
     * function.
     * <p>More information can be found in the
     * <a href=https://docs.spring.io/spring-security/site/docs/current/reference/html/core-services.html#core-services-password-encoding>core-services-password-encoding</a>
     * Spring documentation</a>
     *
     * @return the password encoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
