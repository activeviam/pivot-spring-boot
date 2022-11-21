/*
 * (C) ActiveViam 2022
 * ALL RIGHTS RESERVED. This material is the CONFIDENTIAL and PROPRIETARY
 * property of ActiveViam. Any unauthorized use,
 * reproduction or transfer of this material is strictly prohibited
 */
package com.activeviam.apps.security;

import com.qfs.jwt.impl.JwtFilter;
import com.qfs.pivot.servlet.impl.ContextValueFilter;
import com.qfs.server.cfg.IActivePivotConfig;
import com.qfs.server.cfg.IJwtConfig;
import com.qfs.servlet.handlers.impl.NoRedirectLogoutSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.authentication.switchuser.SwitchUserFilter;
import org.springframework.web.filter.CorsFilter;

import static com.activeviam.apps.security.WebSecurityConfig.COOKIE_NAME;

/**
 * @author ActiveViam
 */
@Configuration
public class JwtAuthenticationConfig {

    @Bean
    ActivePivotJwtAuthenticationDsl jwtAuthenticationDsl(IJwtConfig jwtConfig, IActivePivotConfig activePivotConfig) {
        return new ActivePivotJwtAuthenticationDsl(
                (JwtFilter) jwtConfig.jwtFilter(), activePivotConfig.contextValueFilter());
    }

    public static class ActivePivotJwtAuthenticationDsl
            extends AbstractHttpConfigurer<ActivePivotJwtAuthenticationDsl, HttpSecurity> {

        private static final boolean LOGOUT = false;

        private final JwtFilter jwtFilter;

        private final ContextValueFilter contextValueFilter;

        public ActivePivotJwtAuthenticationDsl(JwtFilter jwtFilter, ContextValueFilter contextValueFilter) {
            this.jwtFilter = jwtFilter;
            this.contextValueFilter = contextValueFilter;
        }

        // We do this in init to prevent the chain to automatically create cors
        @Override
        public void init(HttpSecurity builder) throws Exception {
            super.init(builder);
            builder
                    // As of Spring Security 4.0, CSRF protection is enabled by default.
                    .csrf(AbstractHttpConfigurer::disable)
                    .cors(Customizer.withDefaults());
            if (LOGOUT) {
                // Configure logout URL
                builder.logout(httpSecurityLogoutConfigurer -> httpSecurityLogoutConfigurer
                        .permitAll()
                        .deleteCookies(COOKIE_NAME)
                        .invalidateHttpSession(true)
                        .logoutSuccessHandler(new NoRedirectLogoutSuccessHandler()));
            }
            builder.httpBasic(Customizer.withDefaults());
            builder.addFilterAfter(contextValueFilter, SwitchUserFilter.class);
            // To Allow authentication with JW ( Needed for Active UI )
            builder.addFilterAfter(jwtFilter, CorsFilter.class);
        }
    }
}
