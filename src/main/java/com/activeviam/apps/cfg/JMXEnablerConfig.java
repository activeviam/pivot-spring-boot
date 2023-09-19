/*
 * Copyright (C) ActiveViam 2023
 * ALL RIGHTS RESERVED. This material is the CONFIDENTIAL and PROPRIETARY
 * property of ActiveViam Limited. Any unauthorized use,
 * reproduction or transfer of this material is strictly prohibited
 */

package com.activeviam.apps.cfg;

import static com.activeviam.apps.cfg.ApplicationConfig.START_MANAGER;

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
import com.quartetfs.biz.pivot.monitoring.impl.DynamicActivePivotManagerMBean;
import com.quartetfs.fwk.monitoring.jmx.impl.JMXEnabler;

@Configuration
public class JMXEnablerConfig {
    @Autowired
    protected IActivePivotConfig apConfig;

    /**
     * ActivePivot content service spring configuration
     */
    @Autowired
    protected IActivePivotContentServiceConfig apCSConfig;

    @Autowired
    protected IDatastoreConfig datastoreConfig;

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
    @DependsOn(START_MANAGER)
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
}
