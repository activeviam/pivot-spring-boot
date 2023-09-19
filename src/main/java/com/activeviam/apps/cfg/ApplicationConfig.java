/*
 * Copyright (C) ActiveViam 2023
 * ALL RIGHTS RESERVED. This material is the CONFIDENTIAL and PROPRIETARY
 * property of ActiveViam Limited. Any unauthorized use,
 * reproduction or transfer of this material is strictly prohibited
 */
package com.activeviam.apps.cfg;

import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import com.qfs.pivot.content.impl.DynamicActivePivotContentServiceMBean;
import com.qfs.pivot.monitoring.impl.MemoryAnalysisService;
import com.qfs.server.cfg.IActivePivotConfig;
import com.qfs.server.cfg.IDatastoreConfig;
import com.qfs.server.cfg.content.IActivePivotContentServiceConfig;
import com.qfs.server.cfg.impl.ActivePivotServicesConfig;
import com.quartetfs.biz.pivot.monitoring.impl.DynamicActivePivotManagerMBean;
import com.quartetfs.fwk.Registry;
import com.quartetfs.fwk.contributions.impl.ClasspathContributionProvider;
import com.quartetfs.fwk.monitoring.jmx.impl.JMXEnabler;

/**
 * Spring configuration of the ActivePivot Application services
 *
 * @author ActiveViam
 *
 */
@Configuration
public class ApplicationConfig {

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
     * ActivePivot content service spring configuration
     */
    @Autowired
    protected IActivePivotContentServiceConfig apCSConfig;

    @Autowired
    protected IDatastoreConfig datastoreConfig;

    @Autowired
    protected ActivePivotServicesConfig apServiceConfig;

    /**
     * Enable JMX Monitoring for the Datastore
     *
     * @return the {@link JMXEnabler} attached to the datastore
     */
    @Bean
    public JMXEnabler jmxDatastoreEnabler() {
        return new JMXEnabler(datastoreConfig.database());
    }

    /**
     * Enable JMX Monitoring for ActivePivot Components
     *
     * @return the {@link JMXEnabler} attached to the activePivotManager
     */
    @Bean
    @DependsOn(value = "startManager")
    public JMXEnabler jmxActivePivotEnabler() {
        return new JMXEnabler(new DynamicActivePivotManagerMBean(apConfig.activePivotManager()));
    }

    /**
     * Enable JMX Monitoring for the Content Service
     *
     * @return the {@link JMXEnabler} attached to the content service.
     */
    @Bean
    public JMXEnabler jmxActivePivotContentServiceEnabler() {
        // to allow operations from the JMX bean
        return new JMXEnabler(new DynamicActivePivotContentServiceMBean(
                apCSConfig.activePivotContentService(), apConfig.activePivotManager()));
    }

    /**
     * Enable the MAC memory monitoring beans
     *
     * @return the {@link JMXEnabler} attached to the memory monitoring service.
     */
    @Bean
    public JMXEnabler jmxMemoryMonitoringServiceEnabler() {
        return new JMXEnabler(new MemoryAnalysisService(
                this.datastoreConfig.database(),
                this.apConfig.activePivotManager(),
                Paths.get(System.getProperty("java.io.tmpdir"))));
    }

    /**
     *
     * Initialize and start the ActivePivot Manager, after performing all the injections into the ActivePivot plug-ins.
     *
     * @return void
     * @throws Exception
     *             any exception that occurred during the injection, the initialization or the starting
     */
    @Bean
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
