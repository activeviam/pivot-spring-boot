package com.activeviam.apps.activepivot.admin;

import com.activeviam.fwk.ActiveViamRuntimeException;
import com.qfs.content.service.IContentService;
import com.qfs.content.snapshot.impl.ContentServiceSnapshotter;
import com.qfs.pivot.content.IActivePivotContentService;
import com.qfs.pivot.content.impl.ActivePivotContentServiceBuilder;
import com.qfs.server.cfg.content.IActivePivotContentServiceConfig;
import com.qfs.util.impl.QfsFiles;
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
        return activePivotContentService().getContentService().getUnderlying();
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

    private static final String UI_FOLDER = "/ui";
    private static final String CS_INIT_FILE = "cs-init/contentserver-init.json";

    @Bean
    public void initActiveUIFolder() {
        final var service = contentService().withRootPrivileges();

        if (service.get(UI_FOLDER) == null) {

            try {
                new ContentServiceSnapshotter(service)
                        .importSubtree(UI_FOLDER, QfsFiles.getResourceAsStream(CS_INIT_FILE));
                log.info("Initializing the contentServer with the file: [{}].", CS_INIT_FILE);
            } catch (final Exception e) {
                log.error(
                        "Failed to initialize the /ui folder in the contentServer with the file: [{}].",
                        CS_INIT_FILE,
                        e);

                throw new ActiveViamRuntimeException("Failed to initialize the /ui folder in the contentServer.", e);
            }
        }
    }
}
