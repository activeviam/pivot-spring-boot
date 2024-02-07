/*
 * Copyright (C) ActiveViam 2024
 * ALL RIGHTS RESERVED. This material is the CONFIDENTIAL and PROPRIETARY
 * property of ActiveViam Limited. Any unauthorized use,
 * reproduction or transfer of this material is strictly prohibited
 */
package com.activeviam.apps.cfg.security.dsl.local;

import static com.qfs.QfsWebUtils.url;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;

import com.activeviam.apps.cfg.security.SpringBootContextValueFilter;
import com.activeviam.apps.cfg.security.SpringBootJwtFilter;
import com.activeviam.apps.cfg.security.dsl.AActivePivotAuthenticationDsl;
import com.activeviam.apps.cfg.security.filter.SavedRequestAwareTargetUrlAuthenticationSuccessHandler;

/**
 * @author ActiveViam
 */
public class FormLoginActivePivotAuthenticationDsl extends AActivePivotAuthenticationDsl {
    private final SavedRequestAwareTargetUrlAuthenticationSuccessHandler authenticationSuccessHandler;
    private final Customizer<LogoutConfigurer<HttpSecurity>> logoutConfigurerCustomizer;
    private final MvcRequestMatcher.Builder mvc;

    public FormLoginActivePivotAuthenticationDsl(
            SpringBootJwtFilter jwtFilter,
            SpringBootContextValueFilter contextValueFilter,
            SavedRequestAwareTargetUrlAuthenticationSuccessHandler authenticationSuccessHandler,
            @Autowired(required = false) Customizer<LogoutConfigurer<HttpSecurity>> logoutConfigurerCustomizer,
            MvcRequestMatcher.Builder mvc) {
        super(jwtFilter, contextValueFilter);
        this.authenticationSuccessHandler = authenticationSuccessHandler;
        this.logoutConfigurerCustomizer = logoutConfigurerCustomizer;
        this.mvc = mvc;
    }

    @Override
    public void addAuthentication(HttpSecurity httpSecurity) throws Exception {
        if (mvc != null) {
            httpSecurity.authorizeHttpRequests(auth -> auth.requestMatchers(mvc.pattern(url("login"))));
        }
        if (logoutConfigurerCustomizer != null) {
            httpSecurity.logout(logoutConfigurerCustomizer);
        }
        httpSecurity.formLogin().successHandler(authenticationSuccessHandler).permitAll();
    }
}
