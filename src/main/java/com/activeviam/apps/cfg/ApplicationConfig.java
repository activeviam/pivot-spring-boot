package com.activeviam.apps.cfg;


import com.activeviam.apps.cfg.pivot.PivotManagerConfig;
import com.qfs.pivot.content.impl.DynamicActivePivotContentServiceMBean;
import com.qfs.pivot.monitoring.impl.MemoryAnalysisService;
import com.qfs.server.cfg.IDatastoreConfig;
import com.qfs.server.cfg.content.IActivePivotContentServiceConfig;
import com.qfs.server.cfg.impl.ActivePivotConfig;
import com.qfs.server.cfg.impl.ActivePivotServicesConfig;
import com.quartetfs.biz.pivot.impl.PeriodicActivePivotSchemaRebuilder;
import com.quartetfs.biz.pivot.monitoring.impl.DynamicActivePivotManagerMBean;
import com.quartetfs.fwk.Registry;
import com.quartetfs.fwk.contributions.impl.ClasspathContributionProvider;
import com.quartetfs.fwk.monitoring.jmx.impl.JMXEnabler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;

import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

/**
 * Spring configuration of the ActivePivot Application services
 *
 * @author ActiveViam
 *
 */
@Configuration
@Import(value = {
        ActivePivotWebMvcConfigurer.class,
        SecurityConfig.class,
        SourceConfig.class,
        DatastoreDescriptionConfig.class,
        PivotManagerConfig.class,
        LocalContentServiceConfig.class,
        ActiveUIResourceServerConfig.class,
        ParquetLoaderConfig.class
})
public class ApplicationConfig {

    /* Before anything else we statically initialize the Quartet FS Registry. */
    static {
        Registry.setContributionProvider(new ClasspathContributionProvider("com.qfs", "com.quartetfs", "com.activeviam"));
    }

    @Autowired
    protected ActivePivotConfig apConfig;

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
    public JMXEnabler JMXDatastoreEnabler() {
        return new JMXEnabler(datastoreConfig.datastore());
    }

    /**
     * Enable JMX Monitoring for ActivePivot Components
     *
     * @return the {@link JMXEnabler} attached to the activePivotManager
     */
    @Bean
    @DependsOn(value = "startManager")
    public JMXEnabler JMXActivePivotEnabler() {
        return new JMXEnabler(new DynamicActivePivotManagerMBean(apConfig.activePivotManager()));
    }

    /**
     * Enable JMX Monitoring for the Content Service
     *
     * @return the {@link JMXEnabler} attached to the content service.
     */
    @Bean
    public JMXEnabler JMXActivePivotContentServiceEnabler() {
        // to allow operations from the JMX bean
        return new JMXEnabler(
                new DynamicActivePivotContentServiceMBean(
                        apCSConfig.activePivotContentService(),
                        apConfig.activePivotManager()));
    }

    /**
     * Enable the MAC memory monitoring beans
     * @return the {@link JMXEnabler} attached to the memory monitoring service.
     */
    @Bean
    public JMXEnabler JMXMemoryMonitoringServiceEnabler() {
        return new JMXEnabler(new MemoryAnalysisService(
                this.datastoreConfig.datastore(),
                this.apConfig.activePivotManager(),
                this.datastoreConfig.datastore().getEpochManager(),
                Paths.get(System.getProperty("java.io.tmpdir"))));
    }

    /**
     * Example of bean which periodically rebuild the cubes.
     * <p>
     * This method is OPTIONAL to the function of ActivePivot. In fact, the ActivePivot rebuild
     * feature is used to reclaim space from append only structure of ActivePivot. It does not need
     * to be called very often.
     *
     * @return a bean which periodically rebuild the cubes
     */
    @Bean(initMethod = "start", destroyMethod = "stop")
    public PeriodicActivePivotSchemaRebuilder rebuild() {
        return new PeriodicActivePivotSchemaRebuilder().setManager(apConfig.activePivotManager())
                .setSchemaName(PivotManagerConfig.SCHEMA_NAME)
                .setPeriod(1, TimeUnit.HOURS);
    }

    /**
     *
     * Initialize and start the ActivePivot Manager, after performing all the injections into the ActivePivot plug-ins.
     *
     * @return void
     * @throws Exception any exception that occurred during the injection, the initialization or the starting
     */
    @Bean
    public Void startManager() throws Exception {

        /* ********************************************************************** */
        /* Inject dependencies before the ActivePivot components are initialized. */
        /* ********************************************************************** */
        apManagerInitPrerequisitePluginInjections();

        /* *********************************************** */
        /* Initialize the ActivePivot Manager and start it */
        /* *********************************************** */
        apConfig.activePivotManager().init(null);
        apConfig.activePivotManager().start();

        return null;
    }

    /**
     * Extended plugin injections that are required before doing the startup of the
     * ActivePivot manager.
     *
     * @see #startManager()
     * @throws Exception any exception that occurred during the injection
     */
    protected void apManagerInitPrerequisitePluginInjections() throws Exception {

    }

}
