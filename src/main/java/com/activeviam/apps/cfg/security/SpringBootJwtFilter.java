/*
 * Copyright (C) ActiveViam 2024
 * ALL RIGHTS RESERVED. This material is the CONFIDENTIAL and PROPRIETARY
 * property of ActiveViam Limited. Any unauthorized use,
 * reproduction or transfer of this material is strictly prohibited
 */
package com.activeviam.apps.cfg.security;

import java.io.IOException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.Assert;
import org.springframework.web.filter.GenericFilterBean;

import com.qfs.jwt.impl.JwtAuthentication;

import lombok.extern.slf4j.Slf4j;

/**
 * @author ActiveViam
 */
@Slf4j
public class SpringBootJwtFilter extends GenericFilterBean implements SmartInitializingSingleton {
    /**
     * The list of bearers we allow for a Jwt token. We use Bearer to behave like Keycloak.
     *
     * <p>Jwt is here for historical reasons and will disappear in a future release.
     *
     * <p>The order of this list is important as we take the first one when we create an Authorization
     * header content in {@link #BEARER}.
     * So we put as first item the one that we will keep for the long term.
     */
    protected static final List<String> ALLOWED_BEARERS = Arrays.asList("Bearer", "Jwt");
    /**
     * The bearer to use when creating Authorization headers.
     */
    public static final String BEARER = ALLOWED_BEARERS.get(0);

    /**
     * the key of the claim containing the authorities in an array.
     */
    protected final String authoritiesClaimKey;
    /**
     * the key of the claim containing the principal.
     */
    protected final String principalClaimKey;

    protected final ApplicationContext applicationContext;
    /**
     * Instance providing details about users.
     */
    protected AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource =
            new WebAuthenticationDetailsSource();
    /**
     * Endpoint handling responses for unauthenticated requests.
     *
     * <p>This is used to return the appropriate response or code in case of a request without a
     * valid authentication.
     */
    protected AuthenticationEntryPoint authenticationEntryPoint = (request, response, authException) ->
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token: " + authException.getMessage());

    protected AuthenticationManager authenticationManager;

    /**
     * Constructor.
     *
     * @param principalClaimKey   the key of the claim containing the principal
     * @param authoritiesClaimKey the key of the claim containing the authorities in an array
     */
    public SpringBootJwtFilter(
            ApplicationContext applicationContext, String principalClaimKey, String authoritiesClaimKey) {
        this.applicationContext = applicationContext;
        this.principalClaimKey = principalClaimKey;
        Assert.notNull(principalClaimKey, "principalClaimKey is required");
        this.authoritiesClaimKey = authoritiesClaimKey;
        Assert.notNull(authoritiesClaimKey, "authoritiesClaimKey is required");
    }

    // https://blog.trifork.com/2022/02/25/getting-out-of-a-codependent-relationship-or-how-i-moved-to-a-healthy-component-based-spring-security-configuration/
    @Override
    public void afterSingletonsInstantiated() {
        authenticationManager = applicationContext.getBean(AuthenticationManager.class);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        var req = (HttpServletRequest) request;
        var res = (HttpServletResponse) response;

        try {
            var authorizationHeader = req.getHeader("Authorization");
            if (authorizationHeader == null) {
                chain.doFilter(request, response);
                return;
            }
            var authorizationBearerAndToken = authorizationHeader.split(" ");
            if (!ALLOWED_BEARERS.contains(authorizationBearerAndToken[0])) {
                chain.doFilter(request, response);
                return;
            }

            // Extract token
            var stringToken = authorizationBearerAndToken[1];
            AbstractAuthenticationToken token;
            try {
                token = createAuthentication(stringToken);
            } catch (AuthenticationException e) {
                log.debug("Authentication failed in JWT filter", e);
                authenticationEntryPoint.commence(req, res, e);
                return;
            }

            // Like BasicAuthenticationFilter
            token.setDetails(authenticationDetailsSource.buildDetails(req));
            var auth = authenticate(token);
            SecurityContextHolder.getContext().setAuthentication(auth);

        } catch (AuthenticationException e) {
            SecurityContextHolder.clearContext();
            log.debug("Authentication failed in JWT filter", e);
            authenticationEntryPoint.commence(req, res, e);
            return;
        }

        // Everything went well, proceed with query
        chain.doFilter(request, response);
    }

    /**
     * Attempts to authenticate the passed token with the underlying {@link #authenticationManager}.
     *
     * @param token the authentication request object
     * @return a fully authenticated object including credentials
     * @throws AuthenticationException if authentication fails
     * @see AuthenticationManager#authenticate(Authentication)
     */
    protected Authentication authenticate(Authentication token) throws AuthenticationException {
        return authenticationManager.authenticate(token);
    }

    /**
     * Creates the {@link Authentication} object.
     *
     * @param token the token used to create the result
     * @return the {@link Authentication} object
     * @throws AuthenticationException if the creation failed
     */
    protected AbstractAuthenticationToken createAuthentication(String token) throws AuthenticationException {
        try {
            return new JwtAuthentication(token, principalClaimKey, authoritiesClaimKey);
        } catch (ParseException e) {
            throw new BadCredentialsException("Invalid token", e);
        }
    }

    /**
     * Sets the {@link AuthenticationDetailsSource}.
     *
     * @param authenticationDetailsSource the {@link AuthenticationDetailsSource} to set
     */
    public void setAuthenticationDetailsSource(
            AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource) {
        Assert.notNull(authenticationDetailsSource, "AuthenticationDetailsSource required");
        this.authenticationDetailsSource = authenticationDetailsSource;
    }

    /**
     * Sets the {@link AuthenticationEntryPoint}.
     *
     * @param authenticationEntryPoint the {@link AuthenticationEntryPoint} to set
     */
    public void setAuthenticationEntryPoint(AuthenticationEntryPoint authenticationEntryPoint) {
        Assert.notNull(authenticationDetailsSource, "AuthenticationEntryPoint required");
        this.authenticationEntryPoint = authenticationEntryPoint;
    }
}
