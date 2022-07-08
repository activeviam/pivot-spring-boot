package com.activeviam.apps.cfg;

import com.activeviam.apps.cfg.security.UserDetailsConfig;
import com.activeviam.fwk.ActiveViamRuntimeException;
import com.qfs.content.service.IContentService;
import com.qfs.content.snapshot.impl.ContentServiceSnapshotter;
import com.qfs.pivot.content.IActivePivotContentService;
import com.qfs.pivot.content.impl.ActivePivotContentServiceBuilder;
import com.qfs.server.cfg.content.IActivePivotContentServiceConfig;
import com.qfs.util.impl.QfsFiles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.Properties;

@Configuration
public class LocalContentServiceConfig implements IActivePivotContentServiceConfig {

	private static final Logger logger = LoggerFactory.getLogger(LocalContentServiceConfig.class);

	@Autowired
	protected Environment env;

	@ConfigurationProperties(prefix = "content")
	@Bean
	public Properties contentServiceProperties() {
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
		final var hibernateProperties = contentServiceProperties();
		return new ActivePivotContentServiceBuilder()
				.withPersistence(new org.hibernate.cfg.Configuration()
						.addProperties(hibernateProperties))
				.withAudit()
				.withCacheForEntitlements(Long.parseLong(env.getProperty("contentServer.security.cache.entitlementsTTL", "3600")))
				.needInitialization(UserDetailsConfig.ROLE_ADMIN, UserDetailsConfig.ROLE_ADMIN).build();
	}

	private static final String UI_FOLDER = "/ui";
	private static final String CS_INIT_FILE = "ContentServerInit/contentserver-init.json";

	@Bean
	public void initActiveUIFolder() {
		final var service = contentService().withRootPrivileges();

		if (service.get(UI_FOLDER) == null) {

			try {
				new ContentServiceSnapshotter(service).importSubtree(
						UI_FOLDER, QfsFiles.getResourceAsStream(CS_INIT_FILE));
				logger.info("Initializing the contentServer with the file: [{}].", CS_INIT_FILE);
			} catch (final Exception e) {
				logger.error("Failed to initialize the /ui folder in the contentServer with the file: [{}].", CS_INIT_FILE, e);

				throw new ActiveViamRuntimeException(
						"Failed to initialize the /ui folder in the contentServer.", e);
			}
		}
	}
}
