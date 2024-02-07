/*
 * Copyright (C) ActiveViam 2024
 * ALL RIGHTS RESERVED. This material is the CONFIDENTIAL and PROPRIETARY
 * property of ActiveViam Limited. Any unauthorized use,
 * reproduction or transfer of this material is strictly prohibited
 */
package com.activeviam.apps.cfg.security.dsl.local;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import com.qfs.servlet.handlers.impl.NoRedirectLogoutSuccessHandler;

@Component
public class LogoutConfigurerCustomizer implements Customizer<LogoutConfigurer<HttpSecurity>> {
    public static final String COOKIE_NAME = "JSESSIONID";
    private static final String COOKIE_NAME_PROPERTY = "server.servlet.session.cookie.name";

    private final LogoutSuccessHandler logoutSuccessHandler;
    private final String cookieName;

    public LogoutConfigurerCustomizer(
            ApplicationContext applicationContext,
            @Autowired(required = false) LogoutSuccessHandler logoutSuccessHandler) {
        this.logoutSuccessHandler =
                ObjectUtils.defaultIfNull(logoutSuccessHandler, new NoRedirectLogoutSuccessHandler());
        cookieName = applicationContext.getEnvironment().getProperty(COOKIE_NAME_PROPERTY, COOKIE_NAME);
    }

    @Override
    public void customize(LogoutConfigurer<HttpSecurity> httpSecurityLogoutConfigurer) {
        httpSecurityLogoutConfigurer
                .permitAll()
                .deleteCookies(cookieName)
                .invalidateHttpSession(true)
                .logoutSuccessHandler(logoutSuccessHandler);
    }
}
