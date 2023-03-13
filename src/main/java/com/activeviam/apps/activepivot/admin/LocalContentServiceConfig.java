package com.activeviam.apps.activepivot.admin;

import com.activeviam.spring.config.activeui.ActiveUIContentServiceUtil;
import com.qfs.content.service.IContentService;
import com.qfs.pivot.content.IActivePivotContentService;
import com.qfs.pivot.content.impl.ActivePivotContentServiceBuilder;
import com.qfs.server.cfg.content.IActivePivotContentServiceConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
@EnableConfigurationProperties(ContentServerProperties.class)
public class LocalContentServiceConfig implements IActivePivotContentServiceConfig {

    private final ContentServerProperties contentServerProperties;

    public LocalContentServiceConfig(ContentServerProperties contentServerProperties) {
        this.contentServerProperties = contentServerProperties;
    }

    @Override
    @Bean
    public IContentService contentService() {
        final var contentService = activePivotContentService().getContentService().getUnderlying();
        // initialize the ActiveUI structure required on the ContentService side
        ActiveUIContentServiceUtil.initialize(contentService);
        log.info("Initializing the contentServer with the required structure to work with ActiveUI.");
        return contentService;
    }
    @Override
    @Bean
    public IActivePivotContentService activePivotContentService() {
        return new ActivePivotContentServiceBuilder()
                .withPersistence(
                        new org.hibernate.cfg.Configuration().addProperties(contentServerProperties.getDataSource()))
                .withAudit()
                .withCacheForEntitlements(contentServerProperties.getSecurity().getCacheEntitlementsTTL())
                .needInitialization(
                        contentServerProperties.getSecurity().getCalculatedMemberRole(),
                        contentServerProperties.getSecurity().getKpiRole())
                .build();
    }
}
