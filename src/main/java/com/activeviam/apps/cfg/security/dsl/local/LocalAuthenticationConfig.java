/*
 * Copyright (C) ActiveViam 2024
 * ALL RIGHTS RESERVED. This material is the CONFIDENTIAL and PROPRIETARY
 * property of ActiveViam Limited. Any unauthorized use,
 * reproduction or transfer of this material is strictly prohibited
 */
package com.activeviam.apps.cfg.security.dsl.local;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;

import com.activeviam.apps.cfg.security.SpringBootContextValueFilter;
import com.activeviam.apps.cfg.security.SpringBootJwtFilter;
import com.activeviam.apps.cfg.security.dsl.AuthenticationDslProvider;
import com.activeviam.apps.cfg.security.filter.SavedRequestAwareTargetUrlAuthenticationSuccessHandler;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author ActiveViam
 */
@ConditionalOnLocalAuthentication
@Configuration
@NoArgsConstructor
@Slf4j
public class LocalAuthenticationConfig {

    @Bean
    public AuthenticationDslProvider authenticationDslProvider(
            SpringBootJwtFilter jwtFilter,
            SpringBootContextValueFilter contextValueFilter,
            SavedRequestAwareTargetUrlAuthenticationSuccessHandler authenticationSuccessHandler,
            @Autowired(required = false) Customizer<LogoutConfigurer<HttpSecurity>> logoutConfigurerCustomizer,
            MvcRequestMatcher.Builder mvc) {
        var nothingDsl = new NothingActivePivotAuthenticationDsl();
        var uiDsl = new FormLoginActivePivotAuthenticationDsl(
                jwtFilter, contextValueFilter, authenticationSuccessHandler, logoutConfigurerCustomizer, null);
        var coreDsl = new FormLoginActivePivotAuthenticationDsl(
                jwtFilter, contextValueFilter, authenticationSuccessHandler, logoutConfigurerCustomizer, mvc);
        var excelDsl = new BasicAuthActivePivotAuthenticationDsl(
                null, contextValueFilter, new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED));
        return new AuthenticationDslProvider(nothingDsl, uiDsl, coreDsl, excelDsl);
    }
}
