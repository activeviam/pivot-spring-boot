/*
 * (C) ActiveViam 2019
 * ALL RIGHTS RESERVED. This material is the CONFIDENTIAL and PROPRIETARY
 * property of ActiveViam. Any unauthorized use,
 * reproduction or transfer of this material is strictly prohibited
 */
package com.activeviam.apps.pivotspringboot.activepivot;

import com.google.common.collect.ImmutableList;
import com.qfs.security.cfg.ICorsFilterConfig;
import com.qfs.security.impl.SpringCorsFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.servlet.Filter;
import javax.servlet.ServletException;
import javax.ws.rs.core.HttpHeaders;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author ActiveViam
 */
@Configuration
public class CustomCorsConfigProvider implements ICorsFilterConfig {

	@Override
	public Filter corsFilter() {
		return null;
	}

	@Override
	public Collection<String> getAllowedOrigins() {
		return new ArrayList<>();
	}


	public static List<String> getAllowedHeaders() {
		final List<String> headers = new ArrayList<>();
		headers.addAll(Arrays.asList(SpringCorsFilter.DEFAULT_ALLOWED_HTTP_HEADERS.split(",")));
		// See JwtFilter
		headers.add("X-ActiveUI-Version");
		// Credentials are allowed, requests with Authorization headers will be made
		headers.add(HttpHeaders.AUTHORIZATION);
		headers.add("Access-Control-Allow-Origin");
		headers.add("Cache-Control");
		headers.add("Content-Type");
		return headers;
	}

	// Global cors configuration used by SpringMVC
	// This is almost the same as in ACorsFilterConfig
	@Bean
	CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(ImmutableList.of(CorsConfiguration.ALL));
		configuration.setAllowedMethods(Stream.of(HttpMethod.GET, HttpMethod.POST, HttpMethod.PUT, HttpMethod.DELETE, HttpMethod.PATCH, HttpMethod.HEAD).map(HttpMethod::name).collect(Collectors.toList()));
		configuration.setAllowCredentials(true);
		configuration.setExposedHeaders(ImmutableList.of(org.springframework.http.HttpHeaders.LOCATION));
		// This required is because in ICorsFilter we consider empty list to allow ALL instead of none...
		// and we need ICorsFilter bean to configure Websockets
		if (CustomCorsConfigProvider.getAllowedHeaders() == null || CustomCorsConfigProvider.getAllowedHeaders().isEmpty()){
			configuration.setAllowedHeaders(ImmutableList.of(CorsConfiguration.ALL));
		}
		else {
			configuration.setAllowedHeaders(CustomCorsConfigProvider.getAllowedHeaders());
		}
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}
}
