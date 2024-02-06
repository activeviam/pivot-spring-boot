/*
 * Copyright (C) ActiveViam 2024
 * ALL RIGHTS RESERVED. This material is the CONFIDENTIAL and PROPRIETARY
 * property of ActiveViam Limited. Any unauthorized use,
 * reproduction or transfer of this material is strictly prohibited
 */
package com.activeviam.apps.cfg.security.filter;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.stereotype.Component;

import lombok.Setter;

/**
 * Specific implementation to handle the target URL parameter set into the saved request. This is mandatory as
 * the {@link org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler} implementation
 * used the target URL from the current request for some reason.
 */
@Component
@Setter
public class SavedRequestAwareTargetUrlAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    public static final String TARGET_URL_PARAM = "redirectUrl";
    private final RequestCache requestCache = new HttpSessionRequestCache();

    public SavedRequestAwareTargetUrlAuthenticationSuccessHandler() {
        setTargetUrlParameter(TARGET_URL_PARAM);
    }

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws ServletException, IOException {
        var savedRequest = requestCache.getRequest(request, response);
        if (savedRequest == null) {
            super.onAuthenticationSuccess(request, response, authentication);
            return;
        }
        clearAuthenticationAttributes(request);
        // First try to get the target URL parameter from the saved request. If we cannot find one, we use the redirect
        // URL
        var targets = savedRequest.getParameterValues(getTargetUrlParameter());
        var targetUrl = ArrayUtils.isNotEmpty(targets) ? targets[0] : savedRequest.getRedirectUrl();
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}
