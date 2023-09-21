/*
 * Copyright (C) ActiveViam 2023
 * ALL RIGHTS RESERVED. This material is the CONFIDENTIAL and PROPRIETARY
 * property of ActiveViam Limited. Any unauthorized use,
 * reproduction or transfer of this material is strictly prohibited
 */
package com.activeviam.apps.cfg.content;

import org.hibernate.cfg.AvailableSettings;
import org.hibernate.hikaricp.internal.HikariConfigurationUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import com.activeviam.pivot.tracing.TracingConfig;
import com.activeviam.spring.config.activeui.ActiveUIContentServiceUtil;
import com.qfs.content.service.IContentService;
import com.qfs.content.service.impl.AHibernateContentService;
import com.qfs.content.service.impl.HibernateContentService;
import com.qfs.pivot.content.IActivePivotContentService;
import com.qfs.pivot.content.impl.ActivePivotContentServiceBuilder;
import com.qfs.server.cfg.content.IActivePivotContentServiceConfig;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class LocalContentServiceConfig implements IActivePivotContentServiceConfig {
    private final EmbeddedContentServerHibernateProperties embeddedContentServerHibernateProperties;
    private final ContentServiceSecurityProperties contentServiceSecurityProperties;

    @Override
    @Bean
    @DependsOn(TracingConfig.TRACING_BEAN)
    public IContentService contentService() {
        var contentService = new HibernateContentService(getHibernateConfiguration());
        // initialize the ActiveUI structure required on the ContentService side
        ActiveUIContentServiceUtil.initialize(contentService);
        return contentService;
    }

    private org.hibernate.cfg.Configuration getHibernateConfiguration() {
        log.info(
                "Will be connecting to Content Server DB located at: {}",
                embeddedContentServerHibernateProperties.getProperty(
                        HikariConfigurationUtil.CONFIG_PREFIX + "dataSource.url"));
        var configuration =
                new org.hibernate.cfg.Configuration().addProperties(embeddedContentServerHibernateProperties);
        configuration
                .getProperties()
                .putIfAbsent(AvailableSettings.STATEMENT_BATCH_SIZE, AHibernateContentService.INSERT_BATCH_SIZE);
        return configuration;
    }

    @Override
    @Bean(destroyMethod = "close")
    public IActivePivotContentService activePivotContentService() {
        var cs = contentService();
        return new ActivePivotContentServiceBuilder()
                .with(cs)
                .withCacheForEntitlements(contentServiceSecurityProperties
                        .getCacheEntitlementsTtl()
                        .toSeconds())
                .needInitialization(
                        contentServiceSecurityProperties.getCalculatedMemberRole(),
                        contentServiceSecurityProperties.getKpiRole())
                .build();
    }
}
