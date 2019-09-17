package com.activeviam.apps.pivotspringboot.activepivot;


import com.qfs.content.cfg.impl.ContentServerResourceServerConfig;
import com.qfs.content.cfg.impl.ContentServerResourceServerConfigV2;
import com.qfs.content.cfg.impl.ContentServerWebSocketServicesConfig;
import com.qfs.pivot.content.impl.DynamicActivePivotContentServiceMBean;
import com.qfs.server.cfg.IDatastoreConfig;
import com.qfs.server.cfg.content.IActivePivotContentServiceConfig;
import com.qfs.server.cfg.impl.*;
import com.qfs.service.store.impl.NoSecurityDatastoreServiceConfig;
import com.quartetfs.biz.pivot.monitoring.impl.DynamicActivePivotManagerMBean;
import com.quartetfs.fwk.Registry;
import com.quartetfs.fwk.contributions.impl.ClasspathContributionProvider;
import com.quartetfs.fwk.monitoring.jmx.impl.JMXEnabler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;

/**
 * Spring configuration of the ActivePivot Application services
 *
 * @author ActiveViam
 *
 */
@PropertySource(value = {
        "classpath:jwt.properties",
        "classpath:content.service.properties"
})
@Configuration
@Import(value = {
        // Core stuff
        ActivePivotWebMvcConfigurer.class,
        CorsFilterConfiguration.class,
        ActivePivotConfig.class,
        DatastoreConfig.class,
        NoSecurityDatastoreServiceConfig.class,
        FullAccessBranchPermissionsManagerConfig.class,

        ActivePivotServicesConfig.class,
        ActivePivotWebSocketServicesConfig.class,
        ContentServerWebSocketServicesConfig.class,
        ContentServerResourceServerConfigV2.class,
        ActiveViamRestServicesConfig.class,
        JwtConfig.class,

        ActivePivotWebServicesConfig.class,
        ActivePivotXmlaServletConfig.class,
        ActivePivotRemotingServicesConfig.class,

        CustomI18nConfig.class,
})
public class PivotConfig {

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
