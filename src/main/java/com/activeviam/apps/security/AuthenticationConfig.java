package com.activeviam.apps.security;

import com.qfs.content.service.IContentService;
import com.qfs.jwt.service.IJwtService;
import com.qfs.server.cfg.impl.JwtConfig;
import com.quartetfs.biz.pivot.security.IAuthorityComparator;
import com.quartetfs.biz.pivot.security.impl.AuthorityComparatorAdapter;
import com.quartetfs.fwk.ordering.impl.CustomComparator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.EnableGlobalAuthentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.util.Arrays;
import java.util.List;

@EnableGlobalAuthentication
@Configuration
@Import(value = {
		JwtConfig.class
})
public class AuthenticationConfig {

	public static final String BASIC_AUTH_BEAN_NAME = "basicAuthenticationEntryPoint";

	/**
	 * Admin role
	 */
	public static final String ROLE_ADMIN = "ROLE_ADMIN";

	public static final String ROLE_USER = "ROLE_USER";
	public static final String ROLE_SHARE = "ROLE_SHARE";

	/**
	 * Content Server Root role
	 */
	public static final String ROLE_CS_ROOT = IContentService.ROLE_ROOT;

	/**
	 * Admin user
	 */
	public static final String USER_ADMIN = "admin";

	public static final String PASSWORD_ADMIN = "admin";

	@Bean
	public PasswordEncoder passwordEncoder() {
		PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
		((DelegatingPasswordEncoder) passwordEncoder).setDefaultPasswordEncoderForMatches(new BCryptPasswordEncoder());
		return passwordEncoder;
	}

	// UserDetailsService Bean implemented as it is needed by jwtAuthenticationProvider to enable connection through jwt
	@Bean
	public UserDetailsService userDetailsService() {
		final InMemoryUserDetailsManager localUserDetailsService = new InMemoryUserDetailsManager();
		localUserDetailsService.createUser(User
				.withUsername(USER_ADMIN)
				.password(passwordEncoder().encode(PASSWORD_ADMIN))
				.authorities(ROLE_USER, ROLE_ADMIN).build());
		return localUserDetailsService;
	}

	/**
	 * Returns the default {@link AuthenticationEntryPoint} to use
	 * for the fallback basic HTTP authentication.
	 *
	 * @return The default {@link AuthenticationEntryPoint} for the
	 * fallback HTTP basic authentication.
	 */
//	@Bean(name = BASIC_AUTH_BEAN_NAME)
//	public AuthenticationEntryPoint basicAuthenticationEntryPoint() {
//		return new BasicAuthenticationEntryPoint();
//	}

	/**
	 * [Bean] Comparator for user roles
	 * <p>
	 * Defines the comparator used by:
	 * </p>
	 * <ul>
	 *   <li>com.quartetfs.biz.pivot.security.impl.ContextValueManager#setAuthorityComparator(IAuthorityComparator)</li>
	 *   <li>{@link IJwtService}</li>
	 * </ul>
	 *
	 * @return a comparator that indicates which authority/role prevails over another. <b>NOTICE -
	 * an authority coming AFTER another one prevails over this "previous" authority.</b>
	 * This authority ordering definition is essential to resolve possible ambiguity when,
	 * for a given user, a context value has been defined in more than one authority
	 * applicable to that user. In such case, it is what has been set for the "prevailing"
	 * authority that will be effectively retained for that context value for that user.
	 */
	@Bean
	public IAuthorityComparator authorityComparator() {
		final CustomComparator<String> comp = new CustomComparator<>();
		comp.setFirstObjects(Arrays.asList(ROLE_CS_ROOT));
		comp.setLastObjects(Arrays.asList(ROLE_ADMIN));
		comp.setLastObjects(Arrays.asList(ROLE_USER));
		return new AuthorityComparatorAdapter(comp);
	}

	@Bean
	public AuthenticationProvider basicAuthenticationProvider() {
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
		provider.setPasswordEncoder(passwordEncoder());
		provider.setUserDetailsService(userDetailsService());
		return provider;
	}


	// NOTE: JWT authentication provider bean is provided by JwtConfig
	@Bean
	public AuthenticationManager globalAuthenticationManager(List<AuthenticationProvider> authenticationProviders) {
		var authenticationManager = new ProviderManager(authenticationProviders);
		authenticationManager.setEraseCredentialsAfterAuthentication(false);
		return authenticationManager;
	}


}
