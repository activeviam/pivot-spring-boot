/*
 * Copyright (C) ActiveViam 2023
 * ALL RIGHTS RESERVED. This material is the CONFIDENTIAL and PROPRIETARY
 * property of ActiveViam Limited. Any unauthorized use,
 * reproduction or transfer of this material is strictly prohibited
 */
package com.activeviam.apps.cfg.security.filter;

import static com.qfs.QfsWebUtils.url;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
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
                .successHandler(new HeaderRefererAuthenticationSuccessHandler())
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
                .successHandler(new HeaderRefererAuthenticationSuccessHandler())
                .permitAll()
                .and();
    }

    @Override
    public HttpSecurity applyExcelAuthentication(HttpSecurity http) throws Exception {
        return http.httpBasic().and();
    }

    private static class HeaderRefererAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
        private RequestCache requestCache = new HttpSessionRequestCache();

        @Override
        public void onAuthenticationSuccess(
                HttpServletRequest request, HttpServletResponse response, Authentication authentication)
                throws ServletException, IOException {
            var savedRequest = requestCache.getRequest(request, response);
            if (savedRequest == null) {
                super.onAuthenticationSuccess(request, response, authentication);
                return;
            }
            var targetUrlParameter = getTargetUrlParameter();
            if (isAlwaysUseDefaultTargetUrl()
                    || (targetUrlParameter != null && StringUtils.hasText(request.getParameter(targetUrlParameter)))) {
                requestCache.removeRequest(request, response);
                super.onAuthenticationSuccess(request, response, authentication);
                return;
            }
            clearAuthenticationAttributes(request);
            // Use the DefaultSavedRequest URL
            var referers = savedRequest.getHeaderValues(HttpHeaders.REFERER);
            var targetUrl = !CollectionUtils.isEmpty(referers) ? referers.get(0) : savedRequest.getRedirectUrl();
            getRedirectStrategy().sendRedirect(request, response, targetUrl);
        }

        public void setRequestCache(RequestCache requestCache) {
            this.requestCache = requestCache;
        }
    }
}
