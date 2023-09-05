package com.activeviam.apps.cfg.security;

import static com.activeviam.apps.cfg.security.CommonWebSecurityFiltersConfig.PATTERN_FORMAT;
import static com.activeviam.apps.cfg.security.CommonWebSecurityFiltersConfig.WILDCARD;
import static com.activeviam.apps.cfg.security.SecurityConstants.*;
import static com.qfs.QfsWebUtils.url;
import static com.qfs.server.cfg.impl.ActivePivotRestServicesConfig.PING_SUFFIX;
import static com.qfs.server.cfg.impl.ActivePivotRestServicesConfig.REST_API_URL_PREFIX;
import static com.qfs.server.cfg.impl.ActivePivotWebSocketServicesConfig.WEB_SOCKET_ENDPOINT;

import com.activeviam.spring.config.activeui.ActiveUIResourceServerConfig;

import com.qfs.content.cfg.impl.ContentServerRestServicesConfig;
import com.qfs.content.cfg.impl.ContentServerWebSocketServicesConfig;
import com.qfs.content.rest.impl.ARestContentServer;
import com.qfs.service.store.IDatabaseRestService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.security.web.util.matcher.RegexRequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityFiltersConfig {
    private final JwtAuthenticationConfigurer jwtAuthenticationConfigurer;

    @Scope("prototype")
    @Bean
    MvcRequestMatcher.Builder mvc(HandlerMappingIntrospector introspector) {
        return new MvcRequestMatcher.Builder(introspector);
    }

    @Order(4)
    public SecurityFilterChain embeddedCSFilterChain(HttpSecurity http, MvcRequestMatcher.Builder mvc)
            throws Exception {
        http
                // Only these URLs must be handled by this HttpSecurity
                .securityMatcher(url(ARestContentServer.CONTENT_NAMESPACE, WILDCARD))
                .authorizeHttpRequests(auth -> auth
                        // The order of the matchers matters
                        .requestMatchers(mvc.pattern(
                                HttpMethod.OPTIONS, url(ContentServerRestServicesConfig.REST_API_URL_PREFIX, WILDCARD)))
                        .permitAll()
                        .requestMatchers(mvc.pattern(url(ARestContentServer.CONTENT_NAMESPACE, WILDCARD)))
                        .hasAnyAuthority(ROLE_USER))
                .apply(jwtAuthenticationConfigurer);
        return http.build();
    }

    @Bean
    @Order(6)
    public SecurityFilterChain xmlaFilterChain(HttpSecurity http) throws Exception {
        var pattern = String.format(PATTERN_FORMAT, "xmla");
        return http.securityMatcher(RegexRequestMatcher.regexMatcher(pattern))
                .authorizeHttpRequests(
                        auth -> auth.requestMatchers(RegexRequestMatcher.regexMatcher(HttpMethod.POST, pattern))
                                .authenticated())
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .httpBasic(Customizer.withDefaults())
                .build();
    }

    @Bean
    @Order(20)
    public SecurityFilterChain activeUIFilterChain(HttpSecurity http) throws Exception {
        var pattern = String.format(PATTERN_FORMAT, ActiveUIResourceServerConfig.DEFAULT_NAMESPACE);
        return http.securityMatcher(RegexRequestMatcher.regexMatcher(pattern))
                .headers(httpSecurityHeadersConfigurer ->
                        httpSecurityHeadersConfigurer.frameOptions().disable())
                .authorizeHttpRequests(auth -> auth.requestMatchers(RegexRequestMatcher.regexMatcher(pattern))
                        .permitAll())
                .build();
    }

    @Bean
    @Order(99)
    public SecurityFilterChain coreActivePivotFilterChain(HttpSecurity http, MvcRequestMatcher.Builder mvc)
            throws Exception {
        http.authorizeHttpRequests(auth -> auth
                        // Allow OPTIONS requests
                        .requestMatchers(mvc.pattern(HttpMethod.OPTIONS, url(REST_API_URL_PREFIX, WILDCARD)))
                        .permitAll()

                        // The ping service is temporarily authenticated (see PIVOT-3149)
                        .requestMatchers(mvc.pattern(url(REST_API_URL_PREFIX, PING_SUFFIX)))
                        .hasAnyAuthority(ROLE_USER, ROLE_TECH)

                        // Content server rest call
                        .requestMatchers(
                                mvc.pattern(url(ContentServerRestServicesConfig.REST_API_URL_PREFIX, WILDCARD)))
                        .hasAnyAuthority(ROLE_USER)
                        // Content server websocket
                        .requestMatchers(mvc.pattern(ContentServerWebSocketServicesConfig.CONTENT_ENDPOINT))
                        .hasAnyAuthority(ROLE_USER)

                        // pivot websocket
                        .requestMatchers(mvc.pattern(url(WEB_SOCKET_ENDPOINT, WILDCARD)))
                        .hasAnyAuthority(ROLE_USER)

                        // datastore rest service to delete branches (no restrictions)
                        .requestMatchers(mvc.pattern(url(
                                REST_API_URL_PREFIX,
                                IDatabaseRestService.DATABASE_NAMESPACE,
                                IDatabaseRestService.BRANCHES,
                                WILDCARD)))
                        .hasAnyAuthority(ROLE_USER)

                        // datastore rest service to see branches names (no restrictions)
                        .requestMatchers(mvc.pattern(url(
                                REST_API_URL_PREFIX,
                                IDatabaseRestService.DATABASE_NAMESPACE,
                                IDatabaseRestService.DISCOVERY,
                                IDatabaseRestService.BRANCHES,
                                WILDCARD)))
                        .hasAnyAuthority(ROLE_USER)

                        // No existing constant for cube in the core
                        .requestMatchers(mvc.pattern(url(REST_API_URL_PREFIX, "cube", WILDCARD)))
                        .hasAnyAuthority(ROLE_USER)

                        // One has to be an admin for all the other URLs
                        .requestMatchers(mvc.pattern(url(WILDCARD)))
                        .hasAnyAuthority(ROLE_ADMIN))
                // Added to allow H2 console to work properly
                .headers()
                .frameOptions()
                .sameOrigin()
                .and()
                .apply(jwtAuthenticationConfigurer);
        return http.build();
    }
}
