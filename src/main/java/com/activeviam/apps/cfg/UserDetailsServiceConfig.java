/*
 * (C) ActiveViam 2017-2022
 * ALL RIGHTS RESERVED. This material is the CONFIDENTIAL and PROPRIETARY
 * property of ActiveViam. Any unauthorized use,
 * reproduction or transfer of this material is strictly prohibited
 */

package com.activeviam.apps.cfg;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.provisioning.InMemoryUserDetailsManagerConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static com.activeviam.apps.cfg.SecurityConfig.*;


/**
 * Spring configuration that defines the users and their associated roles in the application.
 *
 * @author ActiveViam
 */
@Configuration
public class UserDetailsServiceConfig {

	@Autowired
	protected AuthenticationManagerBuilder auth;


	/**
	 * [Bean] Create the users that can access the application.
	 *
	 * @return {@link UserDetailsService user data}
	 */
	/**
	 * [Bean] Create the users that can access the application (noop password encoder)
	 *
	 * @return {@link UserDetailsService user data}
	 */

	@Bean
	public PasswordEncoder passwordEncoder() {
		PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
		((DelegatingPasswordEncoder) passwordEncoder)
				.setDefaultPasswordEncoderForMatches(NoOpPasswordEncoder.getInstance());
		return passwordEncoder;
	}

	// UserDetailsService Bean implemented as it is needed by jwtAuthenticationProvider to enable connection through jwt
	@Bean
	public UserDetailsService userDetailsService() throws Exception {

		InMemoryUserDetailsManagerConfigurer<AuthenticationManagerBuilder> inMemUserDetailsManagerConfig =
				new InMemoryUserDetailsManagerConfigurer<AuthenticationManagerBuilder>()
						.passwordEncoder(passwordEncoder())
						.withUser(USER_ADMIN)
						.password(PASSWORD_ADMIN)
						.authorities(ROLE_ADMIN, ROLE_CS_ROOT, ROLE_USER, ROLE_SHARE)
						.and();

		inMemUserDetailsManagerConfig.configure(auth);

		return inMemUserDetailsManagerConfig.getUserDetailsService();
	}

}
