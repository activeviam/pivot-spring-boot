/*
 * Copyright (C) ActiveViam 2024
 * ALL RIGHTS RESERVED. This material is the CONFIDENTIAL and PROPRIETARY
 * property of ActiveViam Limited. Any unauthorized use,
 * reproduction or transfer of this material is strictly prohibited
 */
package com.activeviam.apps.cfg.security.dsl.local;

import static com.qfs.QfsWebUtils.url;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;

import com.activeviam.apps.cfg.security.SpringBootContextValueFilter;
import com.activeviam.apps.cfg.security.SpringBootJwtFilter;
import com.activeviam.apps.cfg.security.dsl.AActivePivotAuthenticationDsl;
import com.activeviam.apps.cfg.security.filter.SavedRequestAwareTargetUrlAuthenticationSuccessHandler;
import com.qfs.servlet.handlers.impl.NoRedirectLogoutSuccessHandler;

/**
 * @author ActiveViam
 */
public class FormLoginActivePivotAuthenticationDsl extends AActivePivotAuthenticationDsl {
    public static final String COOKIE_NAME = "JSESSIONID";
    private static final String COOKIE_NAME_PROPERTY = "server.servlet.session.cookie.name";

    private final SavedRequestAwareTargetUrlAuthenticationSuccessHandler authenticationSuccessHandler;
    private final LogoutSuccessHandler logoutSuccessHandler;
    private final MvcRequestMatcher.Builder mvc;
    private final String cookieName;

    public FormLoginActivePivotAuthenticationDsl(
            ApplicationContext applicationContext,
            SpringBootJwtFilter jwtFilter,
            SpringBootContextValueFilter contextValueFilter,
            SavedRequestAwareTargetUrlAuthenticationSuccessHandler authenticationSuccessHandler,
            @Autowired(required = false) LogoutSuccessHandler userLogoutSuccessHandler,
            MvcRequestMatcher.Builder mvc) {
        super(jwtFilter, contextValueFilter);
        this.authenticationSuccessHandler = authenticationSuccessHandler;
        logoutSuccessHandler =
                ObjectUtils.defaultIfNull(userLogoutSuccessHandler, new NoRedirectLogoutSuccessHandler());
        this.mvc = mvc;
        cookieName = applicationContext.getEnvironment().getProperty(COOKIE_NAME_PROPERTY, COOKIE_NAME);
    }

    @Override
    public void addAuthentication(HttpSecurity httpSecurity) throws Exception {
        if (mvc != null) {
            httpSecurity.authorizeHttpRequests(auth -> auth.requestMatchers(mvc.pattern(url("login"))));
        }
        httpSecurity
                .logout(httpSecurityLogoutConfigurer -> httpSecurityLogoutConfigurer
                        .permitAll()
                        .deleteCookies(cookieName)
                        .invalidateHttpSession(true)
                        .logoutSuccessHandler(logoutSuccessHandler))
                .formLogin()
                .successHandler(authenticationSuccessHandler)
                .permitAll();
    }
}
