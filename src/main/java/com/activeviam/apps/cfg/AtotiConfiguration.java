/*
 * Copyright (C) ActiveViam 2023-2024
 * ALL RIGHTS RESERVED. This material is the CONFIDENTIAL and PROPRIETARY
 * property of ActiveViam Limited. Any unauthorized use,
 * reproduction or transfer of this material is strictly prohibited
 */
package com.activeviam.apps.cfg;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.activeviam.config.InitAndStartAtotiService;
import com.activeviam.properties.cfg.impl.ActiveViamPropertyFromSpringConfig;
import com.activeviam.spring.config.activeui.ActiveUIResourceServerConfig;
import com.activeviam.spring.config.adminui.AdminUIResourceServerConfig;

@Configuration
@Import(
        value = {
            ActiveViamPropertyFromSpringConfig.class,
            InitAndStartAtotiService.class,
            // Expose Admin UI web application
            AdminUIResourceServerConfig.class,
            // Expose the Atoti UI web application
            ActiveUIResourceServerConfig.class
        })
public class AtotiConfiguration {}
