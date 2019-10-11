package com.activeviam.apps.annotations;

import com.qfs.content.cfg.impl.ContentServerResourceServerConfigV2;
import com.qfs.content.cfg.impl.ContentServerWebSocketServicesConfig;
import com.qfs.server.cfg.i18n.impl.LocalI18nConfig;
import com.qfs.server.cfg.impl.*;
import com.qfs.service.store.impl.NoSecurityDatastoreServiceConfig;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@EnableAutoConfiguration
@ComponentScan("com.activeviam.apps.controllers")
@Configuration
@Import(value = {
        // Core stuff
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
        LocalI18nConfig.class
})
public @interface ActivePivotApplication {

}