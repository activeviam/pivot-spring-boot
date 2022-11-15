package com.activeviam.apps.activepivot.ui;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author ActiveViam
 */
@Configuration
@Slf4j
public class ActiveUIWebMvcConfiguration implements WebMvcConfigurer {
	public static final String ACTIVEUI_NAMESPACE = "ui";

	private static final String ACTIVE_UI_INDEX = "/" + ACTIVEUI_NAMESPACE + "/index.html";

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		log.info("Registering ActiveUI resource handlers");
		registry.addResourceHandler("/" + ACTIVEUI_NAMESPACE + "/**").addResourceLocations("classpath:META-INF/resources/webjars/activeui/");
		registry.addResourceHandler("/" + ACTIVEUI_NAMESPACE + "/env*.js").addResourceLocations("classpath:/static/activeui/");
		registry.addResourceHandler("/" + ACTIVEUI_NAMESPACE + "/extensions*.json").addResourceLocations("classpath:/static/activeui/");
		registry.addResourceHandler("/" + ACTIVEUI_NAMESPACE + "/extensions/mdx-drawer-extension/**").addResourceLocations("classpath:/static/activeui/extensions/mdx-drawer-extension/");
	}

	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		log.info("Registering ActiveUI redirects");
		registry
				.addRedirectViewController("/ui", ACTIVE_UI_INDEX);
		registry
				.addRedirectViewController("/ui/", ACTIVE_UI_INDEX);
		registry.addRedirectViewController("/", ACTIVE_UI_INDEX);
		registry.setOrder(Ordered.HIGHEST_PRECEDENCE);

	}

}
