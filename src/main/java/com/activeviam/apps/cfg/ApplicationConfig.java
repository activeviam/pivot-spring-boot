/*
 * Copyright (C) ActiveViam 2023
 * ALL RIGHTS RESERVED. This material is the CONFIDENTIAL and PROPRIETARY
 * property of ActiveViam Limited. Any unauthorized use,
 * reproduction or transfer of this material is strictly prohibited
 */
package com.activeviam.apps.cfg;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import com.qfs.server.cfg.IActivePivotConfig;
import com.quartetfs.fwk.Registry;
import com.quartetfs.fwk.contributions.impl.ClasspathContributionProvider;

/**
 * Spring configuration of the ActivePivot Application services
 *
 * @author ActiveViam
 *
 */
@Configuration
public class ApplicationConfig {
    public static final String START_MANAGER = "startManager";

    /* Before anything else we statically initialize the Quartet FS Registry. */
    static {
        // TODO
        // Remember to include your package, such as `com.yourdomain`, otherwise the custom plugins from that
        // package will not be available in the application.
        Registry.setContributionProvider(
                new ClasspathContributionProvider("com.activeviam.apm", "com.qfs", "com.quartetfs", "com.activeviam"));
    }

    @Autowired
    protected IActivePivotConfig apConfig;

    /**
     *
     * Initialize and start the ActivePivot Manager, after performing all the injections into the ActivePivot plug-ins.
     *
     * @return void
     * @throws Exception
     *             any exception that occurred during the injection, the initialization or the starting
     */
    @Bean(START_MANAGER)
    @DependsOn(PluginConfig.BEAN_NAME)
    public Void startManager() throws Exception {
        /* *********************************************** */
        /* Initialize the ActivePivot Manager and start it */
        /* *********************************************** */
        apConfig.activePivotManager().init(null);
        apConfig.activePivotManager().start();

        return null;
    }
}
