/*
 * Copyright (C) ActiveViam 2024
 * ALL RIGHTS RESERVED. This material is the CONFIDENTIAL and PROPRIETARY
 * property of ActiveViam Limited. Any unauthorized use,
 * reproduction or transfer of this material is strictly prohibited
 */
package com.activeviam.apps.cfg.security.dsl.local;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
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
            ApplicationContext applicationContext,
            SpringBootJwtFilter jwtFilter,
            SpringBootContextValueFilter contextValueFilter,
            SavedRequestAwareTargetUrlAuthenticationSuccessHandler authenticationSuccessHandler,
            @Autowired(required = false) LogoutSuccessHandler logoutSuccessHandler,
            MvcRequestMatcher.Builder mvc) {
        var nothingDsl = new NothingActivePivotAuthenticationDsl();
        var uiDsl = new FormLoginActivePivotAuthenticationDsl(
                applicationContext,
                jwtFilter,
                contextValueFilter,
                authenticationSuccessHandler,
                logoutSuccessHandler,
                null);
        var coreDsl = new FormLoginActivePivotAuthenticationDsl(
                applicationContext,
                jwtFilter,
                contextValueFilter,
                authenticationSuccessHandler,
                logoutSuccessHandler,
                mvc);
        var excelDsl = new BasicAuthActivePivotAuthenticationDsl(
                null, contextValueFilter, new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED));
        return new AuthenticationDslProvider(nothingDsl, uiDsl, coreDsl, excelDsl);
    }
}
