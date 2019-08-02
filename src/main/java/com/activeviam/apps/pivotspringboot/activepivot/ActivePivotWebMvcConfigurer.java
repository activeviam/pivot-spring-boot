/*
 * (C) ActiveViam 2019
 * ALL RIGHTS RESERVED. This material is the CONFIDENTIAL and PROPRIETARY
 * property of ActiveViam. Any unauthorized use,
 * reproduction or transfer of this material is strictly prohibited
 */
package com.activeviam.apps.pivotspringboot.activepivot;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.config.annotation.*;

/**
 * @author ActiveViam
 */
@EnableWebMvc
@Configuration
public class ActivePivotWebMvcConfigurer implements WebMvcConfigurer {

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/content/env*.js")
				.addResourceLocations("classpath:/static/content/");
		registry.addResourceHandler("/ui/env*.js")
				.addResourceLocations("classpath:/static/activeui/");
		registry.setOrder(Ordered.HIGHEST_PRECEDENCE);
	}



}
