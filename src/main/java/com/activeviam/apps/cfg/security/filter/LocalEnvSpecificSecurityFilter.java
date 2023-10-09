/*
 * Copyright (C) ActiveViam 2023
 * ALL RIGHTS RESERVED. This material is the CONFIDENTIAL and PROPRIETARY
 * property of ActiveViam Limited. Any unauthorized use,
 * reproduction or transfer of this material is strictly prohibited
 */
package com.activeviam.apps.cfg.security.filter;

import static com.qfs.QfsWebUtils.url;

import javax.annotation.PostConstruct;

import org.springframework.context.ApplicationContext;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.stereotype.Component;

@Component
public class LocalEnvSpecificSecurityFilter implements IEnvSpecificSecurityFilter {
    public static final String COOKIE_NAME = "JSESSIONID";
    private static final String COOKIE_NAME_PROPERTY = "server.servlet.session.cookie.name";

    private final ApplicationContext applicationContext;
    private String cookieName;

    public LocalEnvSpecificSecurityFilter(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @PostConstruct
    public void initCookieName() {
        cookieName = applicationContext.getEnvironment().getProperty(COOKIE_NAME_PROPERTY, COOKIE_NAME);
    }

    @Override
    public HttpSecurity applyAuthentication(HttpSecurity http) throws Exception {
        return http.logout(httpSecurityLogoutConfigurer -> httpSecurityLogoutConfigurer
                        .permitAll()
                        .deleteCookies(cookieName)
                        .invalidateHttpSession(true))
                .formLogin()
                .and();
    }

    @Override
    public HttpSecurity applyCoreAuthentication(HttpSecurity http, MvcRequestMatcher.Builder mvc) throws Exception {
        return http.authorizeHttpRequests(auth -> auth.requestMatchers(mvc.pattern(url("login"))))
                .logout(httpSecurityLogoutConfigurer -> httpSecurityLogoutConfigurer
                        .permitAll()
                        .deleteCookies(cookieName)
                        .invalidateHttpSession(true))
                .formLogin()
                .permitAll()
                .and();
    }

    @Override
    public HttpSecurity applyExcelAuthentication(HttpSecurity http) throws Exception {
        return http.httpBasic().and();
    }
}
