/*
 * Copyright (C) ActiveViam 2024
 * ALL RIGHTS RESERVED. This material is the CONFIDENTIAL and PROPRIETARY
 * property of ActiveViam Limited. Any unauthorized use,
 * reproduction or transfer of this material is strictly prohibited
 */
package com.activeviam.apps.cfg.security.dsl.local;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import com.activeviam.apps.cfg.security.dsl.AActivePivotAuthenticationDsl;

/**
 * @author ActiveViam
 */
public class NothingActivePivotAuthenticationDsl extends AActivePivotAuthenticationDsl {

    public NothingActivePivotAuthenticationDsl() {
        super(null, null);
    }

    @Override
    public void addAuthentication(HttpSecurity httpSecurity) {
        // nothing to add
    }
}
