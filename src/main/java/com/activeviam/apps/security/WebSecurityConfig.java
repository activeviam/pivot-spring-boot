/*
 * (C) ActiveViam 2022
 * ALL RIGHTS RESERVED. This material is the CONFIDENTIAL and PROPRIETARY
 * property of ActiveViam. Any unauthorized use,
 * reproduction or transfer of this material is strictly prohibited
 */
package com.activeviam.apps.security;

import com.activeviam.spring.config.activeui.ActiveUIResourceServerConfig;
import com.activeviam.spring.config.adminui.AdminUIResourceServerConfig;
import com.qfs.QfsWebUtils;
import com.qfs.content.cfg.impl.ContentServerRestServicesConfig;
import com.qfs.server.cfg.impl.JwtRestServiceConfig;
import com.qfs.server.cfg.impl.VersionServicesConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;


import static com.activeviam.apps.security.BasicAuthenticationConfig.ROLE_ADMIN;
import static com.activeviam.apps.security.BasicAuthenticationConfig.ROLE_USER;
import static com.qfs.QfsWebUtils.url;
import static com.qfs.server.cfg.impl.ActivePivotRestServicesConfig.PING_SUFFIX;
import static com.qfs.server.cfg.impl.ActivePivotRestServicesConfig.REST_API_URL_PREFIX;
import static com.qfs.server.cfg.impl.ActivePivotWebSocketServicesConfig.WEB_SOCKET_ENDPOINT;

/**
 * @author ActiveViam
 */
@Configuration
@EnableWebSecurity(debug = false)
public class WebSecurityConfig {

	/**
	 * Cookie name for AP
	 */
	public static final String COOKIE_NAME = "AP_JSESSIONID";

	@Autowired
	JwtAuthenticationConfig.ActivePivotJwtAuthenticationDsl jwtAuthenticationDsl;

	@Bean
	@Order(2)
	// Must be done before ActivePivotSecurityConfigurer / ContentServiceSecurityConfigurer (because they match common URLs)
	protected SecurityFilterChain jwtFilterChain(HttpSecurity http) throws Exception {
		http
				// As of Spring Security 4.0, CSRF protection is enabled by default.
				.csrf(AbstractHttpConfigurer::disable)
				// Configure CORS
				.cors(Customizer.withDefaults())
				.antMatcher(JwtRestServiceConfig.REST_API_URL_PREFIX + "/**")
				.authorizeHttpRequests(auth ->
						auth.antMatchers(HttpMethod.OPTIONS, "/**")
								.permitAll()
								.antMatchers("/**")
								.hasAnyAuthority(ROLE_USER))
				.httpBasic(Customizer.withDefaults());

		return http.build();
	}

	@Bean
	@Order(3)
	public SecurityFilterChain activePivotVersionFilterChain(HttpSecurity http) throws Exception {
		http
				.antMatcher(VersionServicesConfig.REST_API_URL_PREFIX + "/**")
				.authorizeHttpRequests(auth ->
						auth.antMatchers("/**")
								.permitAll());
		return http.build();
	}


	@Bean
	@Order(4)
	public SecurityFilterChain embeddedCSFilterChain(HttpSecurity http) throws Exception {
		final String pattern = "/" + ContentServerRestServicesConfig.REST_API_URL_PREFIX;
		http
				// Only theses URLs must be handled by this HttpSecurity
				.antMatcher(pattern + "/**")
				.authorizeHttpRequests(auth ->
						// The order of the matchers matters
						auth.antMatchers(
										HttpMethod.OPTIONS,
										QfsWebUtils.url(ContentServerRestServicesConfig.REST_API_URL_PREFIX + "**"))
								.permitAll()
								.antMatchers(pattern + "/**")
								.hasAnyAuthority(ROLE_USER))
				.apply(jwtAuthenticationDsl);
		return http.build();
	}

	@Bean
	@Order(5)
	public SecurityFilterChain spreadsheetServiceFilterChain(HttpSecurity http) throws Exception {
		final String pattern = "/webservices/SpreadsheetServices";
		http
				.antMatcher(pattern + "/**")
				.authorizeHttpRequests(auth ->
						auth.antMatchers(pattern + "/**")
								.hasAnyAuthority(ROLE_USER))
				.apply(jwtAuthenticationDsl);
		return http.build();
	}


	@Bean
	@Order(6)
	public SecurityFilterChain xmlaFilterChain(HttpSecurity http) throws Exception {
		final String pattern = "^(.{0}|\\/|\\/" + "xmla" + "(\\/.*)?)$";
		http
				.regexMatcher(pattern)
				.authorizeHttpRequests(auth ->
								auth.regexMatchers(HttpMethod.POST,pattern)
								.authenticated()
				)
				.csrf(AbstractHttpConfigurer::disable)
				.cors(Customizer.withDefaults())
			.httpBasic(Customizer.withDefaults());
		return http.build();
	}

	@Bean
	@Order(7)
	public SecurityFilterChain adminUISecurityFilterChain(HttpSecurity http) throws Exception {
		final String pattern = "^(.{0}|\\/|\\/" + AdminUIResourceServerConfig.DEFAULT_NAMESPACE + "(\\/.*)?)$";
		http
				// Only theses URLs must be handled by this HttpSecurity
				.regexMatcher(pattern)
				.headers(httpSecurityHeadersConfigurer ->
						httpSecurityHeadersConfigurer.frameOptions().disable())
				.authorizeHttpRequests(auth ->
						auth.regexMatchers(pattern)
								.permitAll())
				.apply(jwtAuthenticationDsl);
		return http.build();
	}


	@Bean
	@Order(20)
	public SecurityFilterChain activeUIFilterChain(HttpSecurity http) throws Exception {
		final var pattern = "^(.{0}|\\/|\\/" + ActiveUIResourceServerConfig.DEFAULT_NAMESPACE + "(\\/.*)?)$";
		http
				.regexMatcher(pattern)
				.headers(httpSecurityHeadersConfigurer -> httpSecurityHeadersConfigurer.frameOptions().disable())
				.authorizeHttpRequests(auth ->
						auth.regexMatchers(pattern)
								.permitAll());
		return http.build();
	}

	@Bean
	@Order(99)
	public SecurityFilterChain coreActivePivotFilterChain(HttpSecurity http) throws Exception {
		http.authorizeHttpRequests(auth ->
						auth.antMatchers(HttpMethod.OPTIONS, url(REST_API_URL_PREFIX, "**"))
								.permitAll()
								// The ping service is temporarily authenticated (see PIVOT-3149)
								.antMatchers(url(REST_API_URL_PREFIX, PING_SUFFIX))
								.hasAnyAuthority(ROLE_USER)
								// REST services
								.antMatchers(url(REST_API_URL_PREFIX, "**"))
								.hasAnyAuthority(ROLE_USER)
								// WS services
								.antMatchers(url(WEB_SOCKET_ENDPOINT, "**"))
								.hasAnyAuthority(ROLE_USER)
								// One has to be an admin for all the other URLs
								.antMatchers("/**")
								.hasAnyAuthority(ROLE_ADMIN))
				.apply(jwtAuthenticationDsl);
		return http.build();
	}

}
