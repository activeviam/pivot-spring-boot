/*
 * (C) ActiveViam 2019
 * ALL RIGHTS RESERVED. This material is the CONFIDENTIAL and PROPRIETARY
 * property of ActiveViam. Any unauthorized use,
 * reproduction or transfer of this material is strictly prohibited
 */
package com.activeviam.apps.pivotspringboot.activepivot;

import com.google.common.collect.ImmutableList;
import com.qfs.security.cfg.impl.ACorsFilterConfig;
import org.apache.http.protocol.HTTP;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.List;

/**
 * @author ActiveViam
 */
@Configuration
public class CustomCorsConfiguration {

	// This method is here so it can be used to configure websockets as well
	public static List<String> getAllowedOrigins() {
		return ImmutableList.of(CorsConfiguration.ALL);
	}

	// Global CORS configuration used by SpringMVC
	// This is almost the same as in ACorsFilterConfig but note that we don't allow ORIGIN and let it be handled elsewhere
	// and we have a couple of additional headers
	// See this: https://github.com/spring-projects/spring-boot/issues/5834#issuecomment-296370088
	// Note that in all our code we do exactly what they say we shouldn't do, i.e.
	// http.authorizeRequests().antMatchers(HttpMethod.OPTIONS, "/**").permitAll();
	@Bean
	CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(getAllowedOrigins());
		// NOTE: we don't allow ORIGIN! It is taken care of by SpringMVC
		// meaning we don't need this piece of code in the security config:
		// http.authorizeRequests().antMatchers(HttpMethod.OPTIONS, "/**").permitAll();
		configuration.setAllowedMethods(ImmutableList.of(
				HttpMethod.GET.name(),
				HttpMethod.POST.name(),
				HttpMethod.PUT.name(),
				HttpMethod.DELETE.name(),
				HttpMethod.PATCH.name(),
				HttpMethod.HEAD.name()));
		configuration.setAllowCredentials(true);
		configuration.setExposedHeaders(ImmutableList.of(HttpHeaders.LOCATION));
		configuration.setAllowedHeaders(ImmutableList.of(
				HttpHeaders.ORIGIN,
				HttpHeaders.ACCEPT,
				HttpHeaders.CONTENT_TYPE,
				HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD,
				HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS,
				HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN,
				HttpHeaders.AUTHORIZATION,
				HttpHeaders.CACHE_CONTROL,
				"X-ActiveUI-Version",
				"X-Requested-With"));

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		// This is for any path
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}
}
