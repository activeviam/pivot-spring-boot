package com.activeviam.apps.cfg.security.basic;

import com.activeviam.apps.cfg.security.UserDetailsConfig;
import com.activeviam.apps.security.IHttpSecurityProcessor;
import com.qfs.server.cfg.IJwtConfig;
import com.qfs.servlet.handlers.impl.NoRedirectLogoutSuccessHandler;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.web.filter.CorsFilter;

import javax.servlet.Filter;

import static com.activeviam.apps.cfg.security.UserDetailsConfig.*;

@Configuration
@ConditionalOnProperty(name = "security.type", havingValue = "basic", matchIfMissing = true)
public class BasicAuthenticationConfig {

    /**
     * Set to true to allow anonymous access.
     */
    public static final boolean useAnonymous = false;

    public static final boolean logout = false;

    /**
     * Cookie name for AP
     */
    public static final String COOKIE_NAME = "AP_JSESSIONID";

    private final ApplicationContext applicationContext;

    public BasicAuthenticationConfig(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Bean
    public AuthenticationProvider basicAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(basicUserDetailsService());
        return provider;
    }

    @Bean
    @Qualifier(USER_DETAILS_SERVICE_QUALIFIER)
    public UserDetailsService basicUserDetailsService() {
        UserDetails user = User.withDefaultPasswordEncoder()
                .username(USER_ADMIN)
                .password(PASSWORD_ADMIN)
                .authorities(ROLE_ADMIN, ROLE_CS_ROOT, ROLE_USER)
                .build();
        return new InMemoryUserDetailsManager(user);
    }

    @Bean
    public IHttpSecurityProcessor httpSecurityConsumer() {
        return new IHttpSecurityProcessor() {

            @Override
            public void preProcess(HttpSecurity http) throws Exception {
                Filter jwtFilter = applicationContext.getBean(IJwtConfig.class).jwtFilter();

                http
                        // As of Spring Security 4.0, CSRF protection is enabled by default.
                        .csrf().disable()
                        .cors().and()
                        // To allow authentication with JWT (Required for ActiveUI)
                        .addFilterAfter(jwtFilter, CorsFilter.class);

                if (logout) {
                    // Configure logout URL
                    http.logout()
                            .permitAll()
                            .deleteCookies(COOKIE_NAME)
                            .invalidateHttpSession(true)
                            .logoutSuccessHandler(new NoRedirectLogoutSuccessHandler());
                }

                if (useAnonymous) {
                    // Handle anonymous users. The granted authority ROLE_USER
                    // will be assigned to the anonymous request
                    http.anonymous().principal("guest").authorities(UserDetailsConfig.ROLE_USER);
                }
            }

        };
    }

}
