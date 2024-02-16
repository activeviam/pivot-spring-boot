/*
 * Copyright (C) ActiveViam 2024
 * ALL RIGHTS RESERVED. This material is the CONFIDENTIAL and PROPRIETARY
 * property of ActiveViam Limited. Any unauthorized use,
 * reproduction or transfer of this material is strictly prohibited
 */
package com.activeviam.apps.cfg;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PluginConfig {
    public static final String BEAN_NAME = "configurePlugins";

    /**
     * Extended plugin injections that are required before doing the startup of the ActivePivot manager.
     */
    @Bean(BEAN_NAME)
    Void configurePlugins() {
        /* ********************************************************************** */
        /* Inject dependencies before the ActivePivot components are initialized. */
        /* ********************************************************************** */
        // Example:
        // var provider = (ICustomAHDescProvider) Registry.getPluginValue(
        // 					IAnalysisHierarchyDescriptionProvider.class, "YOUR_KEY");
        // ExtendedPluginInjector.inject(IMultiVersionHierarchy.class, "YOUR_KEY", customService);
        return null;
    }
}
