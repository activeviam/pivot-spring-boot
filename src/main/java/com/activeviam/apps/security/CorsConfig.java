/*
 * (C) ActiveViam 2022
 * ALL RIGHTS RESERVED. This material is the CONFIDENTIAL and PROPRIETARY
 * property of ActiveViam. Any unauthorized use,
 * reproduction or transfer of this material is strictly prohibited
 */
package com.activeviam.apps.security;

import com.activeviam.collections.impl.Immutable;
import com.activeviam.security.cfg.ICorsConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * @author ActiveViam
 */
@Configuration
public class CorsConfig implements ICorsConfig {

	@Override
	public List<String> getAllowedOrigins() {
		return Immutable.list(CorsConfiguration.ALL).toList();
	}

	/**
	 * [Bean] Spring standard way of configuring CORS.
	 *
	 * <p>This simply forwards the configuration of {@link ICorsConfig} to Spring security system.
	 *
	 * @return the configuration for the application.
	 */
	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		final CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOriginPatterns(getAllowedOrigins());
		configuration.setAllowedHeaders(getAllowedHeaders());
		configuration.setExposedHeaders(getExposedHeaders());
		configuration.setAllowedMethods(getAllowedMethods());
		configuration.setAllowCredentials(true);

		final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);

		return source;
	}
}
