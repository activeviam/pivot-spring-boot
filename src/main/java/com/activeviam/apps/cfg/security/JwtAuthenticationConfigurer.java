package com.activeviam.apps.cfg.security;

import com.qfs.server.cfg.IActivePivotConfig;
import com.qfs.server.cfg.impl.JwtConfig;
import com.qfs.servlet.handlers.impl.NoRedirectLogoutSuccessHandler;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.authentication.switchuser.SwitchUserFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.CorsFilter;

@Component
public class JwtAuthenticationConfigurer extends AbstractHttpConfigurer<JwtAuthenticationConfigurer, HttpSecurity> {
    public static final String COOKIE_NAME = "JSESSIONID";
    private static final String COOKIE_NAME_PROPERTY = "server.servlet.session.cookie.name";

    private final ApplicationContext applicationContext;
    private final JwtConfig jwtConfig;
    private final IActivePivotConfig activePivotConfig;
    private final SecurityJwtProperties jwtProperties;
    private String cookieName;

    public JwtAuthenticationConfigurer(
            ApplicationContext applicationContext,
            JwtConfig jwtConfig,
            @Autowired(required = false) IActivePivotConfig activePivotConfig,
            SecurityJwtProperties jwtProperties) {
        this.applicationContext = applicationContext;
        this.jwtConfig = jwtConfig;
        this.activePivotConfig = activePivotConfig;
        this.jwtProperties = jwtProperties;
    }

    @PostConstruct
    public void initCookieName() {
        cookieName = applicationContext.getEnvironment().getProperty(COOKIE_NAME_PROPERTY, COOKIE_NAME);
    }

    // We do this in init to prevent the chain to automatically create cors
    @Override
    public void init(HttpSecurity builder) throws Exception {
        super.init(builder);
        builder
                // As of Spring Security 4.0, CSRF protection is enabled by default.
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults());
        if (jwtProperties.isConfigureLogout()) {
            // Configure logout URL
            builder.logout(httpSecurityLogoutConfigurer -> httpSecurityLogoutConfigurer
                    .permitAll()
                    .deleteCookies(cookieName)
                    .invalidateHttpSession(true)
                    .logoutSuccessHandler(new NoRedirectLogoutSuccessHandler()));
        }
        builder.httpBasic(Customizer.withDefaults());
        if (activePivotConfig != null) {
            builder.addFilterAfter(activePivotConfig.contextValueFilter(), SwitchUserFilter.class);
        }
        // To Allow authentication with JW ( Needed for Active UI )
        builder.addFilterAfter(jwtConfig.jwtFilter(), CorsFilter.class);
    }
}
