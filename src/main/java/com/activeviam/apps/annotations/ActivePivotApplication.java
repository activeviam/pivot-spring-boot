package com.activeviam.apps.annotations;

import com.activeviam.apm.cfg.impl.MonitoredDataLoadingConfig;
import com.activeviam.apm.cfg.impl.MonitoringJmxConfig;
import com.activeviam.apm.cfg.impl.QueryPerformanceEvaluatorConfig;
import com.activeviam.properties.cfg.impl.ActiveViamPropertyFromSpringConfig;
import com.activeviam.spring.config.activeui.ActiveUIResourceServerConfig;
import com.activeviam.spring.config.adminui.AdminUIResourceServerConfig;
import com.qfs.server.cfg.i18n.impl.LocalI18nConfig;
import com.qfs.server.cfg.impl.ActivePivotServicesConfig;
import com.qfs.server.cfg.impl.ActivePivotWithDatastoreConfig;
import com.qfs.server.cfg.impl.ActivePivotXmlaServletConfig;
import com.qfs.server.cfg.impl.ActiveViamRestServicesConfig;
import com.qfs.server.cfg.impl.ActiveViamWebSocketServicesConfig;
import com.qfs.server.cfg.impl.FullAccessBranchPermissionsManagerConfig;
import com.qfs.service.store.impl.NoSecurityDatabaseServiceConfig;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@EnableAutoConfiguration
@ComponentScan
@ConfigurationPropertiesScan
@Configuration
@Import(
        value = {
            // Configuration to define core properties from Spring
            ActiveViamPropertyFromSpringConfig.class,

            // Core imports
            ActivePivotWithDatastoreConfig.class,
            ActivePivotServicesConfig.class,

            // Security
            FullAccessBranchPermissionsManagerConfig.class,
            NoSecurityDatabaseServiceConfig.class,

            // REST services for ActiveUI
            ActiveViamRestServicesConfig.class,
            ActiveViamWebSocketServicesConfig.class,

            // Configuration for Excel
            ActivePivotXmlaServletConfig.class,

            // Internationalization
            LocalI18nConfig.class,

            // APM (https://docs.activeviam.com/products/atoti/server/latest/docs/monitoring/application_performance_monitoring/#monitored-spring-configuration)
            MonitoringJmxConfig.class,
            MonitoredDataLoadingConfig.class,
            QueryPerformanceEvaluatorConfig.class,

            // Expose Admin UI web application
            AdminUIResourceServerConfig.class,

            // Expose the Atoti UI web application
            ActiveUIResourceServerConfig.class,
        })
public @interface ActivePivotApplication {}
