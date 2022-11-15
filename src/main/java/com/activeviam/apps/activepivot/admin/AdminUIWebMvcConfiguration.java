/*
 * (C) ActiveViam 2022
 * ALL RIGHTS RESERVED. This material is the CONFIDENTIAL and PROPRIETARY
 * property of ActiveViam. Any unauthorized use,
 * reproduction or transfer of this material is strictly prohibited
 */
package com.activeviam.apps.activepivot.admin;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author ActiveViam
 */
@Configuration
@Slf4j
public class AdminUIWebMvcConfiguration implements WebMvcConfigurer {
	public static final String ADMIN_UI_NAMESPACE = "admin/ui";

	private static final String ADMIN_UI_INDEX = "/" + ADMIN_UI_NAMESPACE + "/index.html";

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		log.info("Registering AdminUI resource handlers");
		registry.addResourceHandler("/" + ADMIN_UI_NAMESPACE + "/**").addResourceLocations("classpath:META-INF/resources/webjars/admin-ui/");
		registry.addResourceHandler("/" + ADMIN_UI_NAMESPACE + "/env*.js").addResourceLocations("classpath:/static/admin/");
	}

	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		log.info("Registering AdminUI redirects");
		registry
				.addRedirectViewController("/admin", ADMIN_UI_INDEX);
		registry
				.addRedirectViewController("/admin/", ADMIN_UI_INDEX);
		registry
				.addRedirectViewController("/admin/ui", ADMIN_UI_INDEX);
		registry
				.addRedirectViewController("/admin/ui/", ADMIN_UI_INDEX);
	}

}
