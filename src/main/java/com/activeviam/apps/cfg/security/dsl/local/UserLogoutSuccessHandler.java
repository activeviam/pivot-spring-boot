/*
 * Copyright (C) ActiveViam 2023-2024
 * ALL RIGHTS RESERVED. This material is the CONFIDENTIAL and PROPRIETARY
 * property of ActiveViam Limited. Any unauthorized use,
 * reproduction or transfer of this material is strictly prohibited
 */
package com.activeviam.apps.cfg.security.dsl.local;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@NoArgsConstructor
@Slf4j
public class UserLogoutSuccessHandler implements LogoutSuccessHandler {

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException {
        if (authentication != null) {
            log.info("User {} has logged out", authentication.getName());
        }
        response.sendRedirect(request.getParameter("redirectUrl"));
    }
}
