/*
 * Copyright (C) ActiveViam 2024
 * ALL RIGHTS RESERVED. This material is the CONFIDENTIAL and PROPRIETARY
 * property of ActiveViam Limited. Any unauthorized use,
 * reproduction or transfer of this material is strictly prohibited
 */
package com.activeviam.apps.cfg.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import com.quartetfs.biz.pivot.security.IContextValuePropagator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author ActiveViam
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class SpringBootContextValueFilter extends GenericFilterBean {
    /**
     * The {@link IContextValuePropagator} responsible for applying user's context values.
     */
    private final IContextValuePropagator contextValuePropagator;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        try {
            if (request instanceof HttpServletRequest httpReq && HttpMethod.OPTIONS.matches(httpReq.getMethod())) {
                // The OPTIONS requests made for CORS are not always authenticated.
                // These requests do not need (and should not need) to access pivots but to be
                // safe the access is not granted to prevent security issues.
                // setUserContext() was either applying no context values or throwing when there was
                // a remote content server (because the call is made by an anonymous user) before
                // this fix.
                contextValuePropagator.forbidAccessToAllCubes();
            } else {
                // Initialize the context for each pivot
                contextValuePropagator.setUserContext();
            }

            // Continue chain filtering
            chain.doFilter(request, response);
        } finally {
            // Clear the context once the servlet has been served
            contextValuePropagator.clearUserContext();
        }
    }
}
