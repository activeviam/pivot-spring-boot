package com.activeviam.apps.cfg.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableWebSecurity
@Import({
        CorsFilterConfig.class,
        UserDetailsConfig.class,
        ActivePivotWebSecurityConfig.class
})
public class SecurityConfig {

    public static final String BASIC_AUTH_BEAN_NAME = "basicAuthenticationEntryPoint";

    @Autowired(required = false)
    protected List<AuthenticationProvider> authenticationProviders = new ArrayList<>();

    @Bean
    public AuthenticationManager globalAuthenticationManager() {
        ProviderManager manager = new ProviderManager(authenticationProviders);
        manager.setEraseCredentialsAfterAuthentication(false);
        return manager;
    }

    /**
     * Returns the default {@link AuthenticationEntryPoint} to use
     * for the fallback basic HTTP authentication.
     *
     * @return The default {@link AuthenticationEntryPoint} for the
     * fallback HTTP basic authentication.
     */
    @Bean(name = BASIC_AUTH_BEAN_NAME)
    public AuthenticationEntryPoint basicAuthenticationEntryPoint() {
        return new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED);
    }

}
