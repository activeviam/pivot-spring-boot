package com.activeviam.apps.cfg;

import com.qfs.content.service.IContentService;
import com.qfs.pivot.content.IActivePivotContentService;
import com.qfs.pivot.content.impl.ActivePivotContentServiceBuilder;
import com.qfs.server.cfg.content.IActivePivotContentServiceConfig;
import com.qfs.util.impl.QfsProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
public class LocalContentServiceConfig implements IActivePivotContentServiceConfig {

    @ConfigurationProperties(prefix = "content")
    @Bean
    public Properties contentServiceProperties(){
        return new Properties();
    }

    @Override
    @Bean
    public IContentService contentService() {
        final IContentService contentService = activePivotContentService().getContentService().getUnderlying();
        return contentService;
    }

    @Override
    @Bean
    public IActivePivotContentService activePivotContentService() {
        final Properties hibernateProperties = contentServiceProperties();
        return new ActivePivotContentServiceBuilder()
                .withPersistence(new org.hibernate.cfg.Configuration().addProperties(hibernateProperties)).withAudit()
                .withoutCache().needInitialization(SecurityConfig.ROLE_ADMIN, SecurityConfig.ROLE_ADMIN).build();
    }
}
