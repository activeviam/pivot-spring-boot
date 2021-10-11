package com.activeviam.apps.cfg;

import com.activeviam.fwk.ActiveViamRuntimeException;
import com.qfs.content.service.IContentService;
import com.qfs.content.snapshot.impl.ContentServiceSnapshotter;
import com.qfs.pivot.content.IActivePivotContentService;
import com.qfs.pivot.content.impl.ActivePivotContentServiceBuilder;
import com.qfs.server.cfg.content.IActivePivotContentServiceConfig;
import com.qfs.util.impl.QfsFiles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.Properties;

@Configuration
public class LocalContentServiceConfig implements IActivePivotContentServiceConfig {

    @Autowired
    Environment env;

    @ConfigurationProperties(prefix = "content")
    @Bean
    public Properties contentServiceProperties(){
        return new Properties();
    }

    @Override
    @Bean
    public IContentService contentService() {
        return activePivotContentService().getContentService().getUnderlying();
    }

    @Override
    @Bean
    public IActivePivotContentService activePivotContentService() {
        final Properties hibernateProperties = contentServiceProperties();
        return new ActivePivotContentServiceBuilder()
                .withPersistence(new org.hibernate.cfg.Configuration()
                        .addProperties(hibernateProperties)).withAudit()
                .withCacheForEntitlements(Long.parseLong(env.getProperty("contentServer.security.cache.entitlementsTTL" ,"3600")))
                .needInitialization(SecurityConfig.ROLE_ADMIN, SecurityConfig.ROLE_ADMIN).build();
    }

    /**
     * Content server initialization for ActiveUI5.
     *
     * <p>This is required when running ActiveUI with an empty
     * ContentServer.
     */
    @Bean
    public void initActiveUIFolder() {
        IContentService service = contentService().withRootPrivileges();
        if (service.get("/ui") == null) {
            try {
                new ContentServiceSnapshotter(service).importSubtree(
                    "/", QfsFiles.getResourceAsStream("initialContent.json"));
            } catch (Exception e) {
                throw new ActiveViamRuntimeException(
                    "Failed to initialize the /ui folder in the contentServer", e);
            }
        }
    }
}