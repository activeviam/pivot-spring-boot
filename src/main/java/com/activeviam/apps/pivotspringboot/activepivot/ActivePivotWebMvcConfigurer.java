/*
 * (C) ActiveViam 2019
 * ALL RIGHTS RESERVED. This material is the CONFIDENTIAL and PROPRIETARY
 * property of ActiveViam. Any unauthorized use,
 * reproduction or transfer of this material is strictly prohibited
 */
package com.activeviam.apps.pivotspringboot.activepivot;

import com.google.common.collect.ImmutableList;
import com.qfs.security.impl.SpringCorsFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.*;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.HttpHeaders;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.qfs.QfsWebUtils.url;
import static com.qfs.server.cfg.impl.ActivePivotRestServicesConfig.REST_API_URL_PREFIX;

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
