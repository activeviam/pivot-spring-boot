package com.activeviam.apps.cfg;

import java.util.Properties;

import com.activeviam.spring.config.activeui.ActiveUIContentServiceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import com.activeviam.fwk.ActiveViamRuntimeException;
import com.qfs.content.service.IContentService;
import com.qfs.content.snapshot.impl.ContentServiceSnapshotter;
import com.qfs.pivot.content.IActivePivotContentService;
import com.qfs.pivot.content.impl.ActivePivotContentServiceBuilder;
import com.qfs.server.cfg.content.IActivePivotContentServiceConfig;
import com.qfs.util.impl.QfsFiles;

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
		final var contentService = activePivotContentService().getContentService().getUnderlying();
		// initialize the ActiveUI structure required on the ContentService side
		ActiveUIContentServiceUtil.initialize(contentService);
		logger.info("Initialized the contentServer with the required structure to work with ActiveUI.");
		return contentService;
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
				.needInitialization(SecurityConfig.ROLE_ADMIN, SecurityConfig.ROLE_ADMIN).build();
	}

}
