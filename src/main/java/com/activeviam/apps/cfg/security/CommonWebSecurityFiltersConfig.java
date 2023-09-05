package com.activeviam.apps.cfg.security;

import static com.activeviam.apps.cfg.security.SecurityConstants.ROLE_ACTUATOR;
import static com.qfs.QfsWebUtils.url;

import com.activeviam.spring.config.adminui.AdminUIResourceServerConfig;
import com.qfs.server.cfg.impl.JwtRestServiceConfig;
import com.qfs.server.cfg.impl.VersionServicesConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.security.web.util.matcher.RegexRequestMatcher;

@Configuration
@RequiredArgsConstructor
public class CommonWebSecurityFiltersConfig {
    public static final String WILDCARD = "**";
    public static final String PATTERN_FORMAT = "^(.{0}|\\/|\\/%s(\\/.*)?)$";

    private final JwtAuthenticationConfigurer jwtAuthenticationConfigurer;

    @Bean
    @Order(1)
    protected SecurityFilterChain actuatorFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                // As of Spring Security 4.0, CSRF protection is enabled by default.
                .csrf(AbstractHttpConfigurer::disable)
                // Configure CORS
                .cors(Customizer.withDefaults())
                // For some reason if we use MvcMatcher, the jolokia endpoint doesn't get picked up...
                .securityMatcher(RegexRequestMatcher.regexMatcher(String.format(PATTERN_FORMAT, "actuator")))
                .authorizeHttpRequests(auth -> auth.anyRequest().hasAnyAuthority(ROLE_ACTUATOR))
                .httpBasic(Customizer.withDefaults())
                .build();
    }

    /**
     * Must be done before ActivePivotSecurityConfigurer / ContentServiceSecurityConfigurer (because they match common
     * URLs)
     *
     * @param http allows to configure the security for HTTP requests
     * @return the filter
     * @throws Exception when the definition is not correct
     */
    @Bean
    @Order(2)
    protected SecurityFilterChain jwtFilterChain(HttpSecurity http, MvcRequestMatcher.Builder mvc) throws Exception {
        // As of Spring Security 4.0, CSRF protection is enabled by default.
        return http.csrf(AbstractHttpConfigurer::disable)
                // Configure CORS
                .cors(Customizer.withDefaults())
                .securityMatcher(mvc.pattern(url(JwtRestServiceConfig.REST_API_URL_PREFIX, WILDCARD)))
                .authorizeHttpRequests(auth -> auth.requestMatchers(mvc.pattern(HttpMethod.OPTIONS, url(WILDCARD)))
                        .permitAll()
                        .requestMatchers(mvc.pattern(url(WILDCARD)))
                        .hasAnyAuthority(SecurityConstants.ROLE_USER))
                .httpBasic(Customizer.withDefaults())
                .build();
    }

    @Bean
    @Order(3)
    public SecurityFilterChain activePivotVersionFilterChain(HttpSecurity http, MvcRequestMatcher.Builder mvc)
            throws Exception {
        return http.securityMatcher(mvc.pattern(url(VersionServicesConfig.REST_API_URL_PREFIX, WILDCARD)))
                .authorizeHttpRequests(
                        auth -> auth.requestMatchers(mvc.pattern(url(WILDCARD))).permitAll())
                .build();
    }

    @Bean
    @Order(8)
    public SecurityFilterChain adminUISecurityFilterChain(HttpSecurity http) throws Exception {
        var pattern = String.format(PATTERN_FORMAT, AdminUIResourceServerConfig.DEFAULT_NAMESPACE);
        http.securityMatcher(new RegexRequestMatcher(pattern, null))
                .headers(httpSecurityHeadersConfigurer ->
                        httpSecurityHeadersConfigurer.frameOptions().disable())
                .authorizeHttpRequests(auth -> auth.requestMatchers(new RegexRequestMatcher(pattern, null))
                        .permitAll())
                .apply(jwtAuthenticationConfigurer);
        return http.build();
    }
}
