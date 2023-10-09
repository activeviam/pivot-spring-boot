/*
 * Copyright (C) ActiveViam 2023
 * ALL RIGHTS RESERVED. This material is the CONFIDENTIAL and PROPRIETARY
 * property of ActiveViam Limited. Any unauthorized use,
 * reproduction or transfer of this material is strictly prohibited
 */
package com.activeviam.apps.cfg.security.filter;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;

public interface IEnvSpecificSecurityFilter {
    HttpSecurity applyAuthentication(HttpSecurity http) throws Exception;

    HttpSecurity applyCoreAuthentication(HttpSecurity http, MvcRequestMatcher.Builder mvc) throws Exception;

    HttpSecurity applyExcelAuthentication(HttpSecurity http) throws Exception;
}
