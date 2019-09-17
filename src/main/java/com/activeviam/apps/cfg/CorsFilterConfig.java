/*
 * (C) ActiveViam 2019
 * ALL RIGHTS RESERVED. This material is the CONFIDENTIAL and PROPRIETARY
 * property of ActiveViam. Any unauthorized use,
 * reproduction or transfer of this material is strictly prohibited
 */
package com.activeviam.apps.cfg;

import com.activeviam.collections.impl.Immutable;
import com.qfs.security.cfg.ICorsFilterConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import javax.servlet.Filter;
import javax.servlet.ServletException;
import java.util.List;

/**
 * @author ActiveViam
 */
@Configuration
public class CorsFilterConfig implements ICorsFilterConfig {

	// This method is here so it can be used to configure websockets as well
	@Override
	public List<String> getAllowedOrigins() {
		return Immutable.list(CorsConfiguration.ALL).toList();
	}

	@Bean
	@Override
	public Filter corsFilter() throws ServletException {
		return new CorsFilter(corsConfigurationSource());
	}

	// Global CORS configuration used by SpringMVC
	// This is almost the same as in ACorsFilterConfig but note that we don't allow ORIGIN and let it be handled elsewhere
	// and we have a couple of additional headers
	// See this: https://github.com/spring-projects/spring-boot/issues/5834#issuecomment-296370088
	// Note that in all our code we do exactly what they say we shouldn't do, i.e.
	// http.authorizeRequests().antMatchers(HttpMethod.OPTIONS, "/**").permitAll();
	@Bean
	CorsConfigurationSource corsConfigurationSource() {
		org.springframework.web.cors.CorsConfiguration configuration = new org.springframework.web.cors.CorsConfiguration();
		configuration.setAllowedOrigins(getAllowedOrigins());
		// NOTE: we don't allow ORIGIN! It is taken care of by SpringMVC
		// meaning we don't need this piece of code in the security config:
		// http.authorizeRequests().antMatchers(HttpMethod.OPTIONS, "/**").permitAll();
		configuration.setAllowedMethods(Immutable.list(
				HttpMethod.GET.name(),
				HttpMethod.POST.name(),
				HttpMethod.PUT.name(),
				HttpMethod.DELETE.name(),
				HttpMethod.PATCH.name(),
				HttpMethod.HEAD.name()).toList());
		configuration.setAllowCredentials(true);
		configuration.setExposedHeaders(Immutable.list(HttpHeaders.LOCATION).toList());
		configuration.setAllowedHeaders(Immutable.list(
				HttpHeaders.ORIGIN,
				HttpHeaders.ACCEPT,
				HttpHeaders.CONTENT_TYPE,
				HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD,
				HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS,
				HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN,
				HttpHeaders.AUTHORIZATION,
				HttpHeaders.CACHE_CONTROL,
				"X-ActiveUI-Version",
				"X-Requested-With").toList());

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		// This is for any path
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}
}
