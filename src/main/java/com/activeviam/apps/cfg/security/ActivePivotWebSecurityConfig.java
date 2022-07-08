package com.activeviam.apps.cfg.security;

import com.activeviam.apps.cfg.ActiveUIResourceServerConfig;
import com.activeviam.apps.security.IHttpSecurityProcessor;
import com.qfs.server.cfg.IActivePivotConfig;
import com.qfs.server.cfg.impl.JwtRestServiceConfig;
import com.qfs.server.cfg.impl.VersionServicesConfig;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.switchuser.SwitchUserFilter;

import static com.activeviam.apps.cfg.security.UserDetailsConfig.ROLE_TECH;
import static com.activeviam.apps.cfg.security.UserDetailsConfig.ROLE_USER;
import static com.activeviam.apps.cfg.security.SecurityConfig.BASIC_AUTH_BEAN_NAME;
import static com.qfs.QfsWebUtils.url;
import static com.qfs.server.cfg.impl.ActivePivotRemotingServicesConfig.*;
import static com.qfs.server.cfg.impl.ActivePivotRestServicesConfig.PING_SUFFIX;
import static com.qfs.server.cfg.impl.ActivePivotRestServicesConfig.REST_API_URL_PREFIX;
import static com.qfs.server.cfg.impl.ActivePivotServicesConfig.*;
import static com.qfs.server.cfg.impl.CxfServletConfig.CXF_WEB_SERVICES;

@Configuration
public class ActivePivotWebSecurityConfig {

    private final ApplicationContext applicationContext;

    private final IActivePivotConfig activePivotConfig;

    private final IHttpSecurityProcessor httpSecurityConsumer;

    public ActivePivotWebSecurityConfig(ApplicationContext applicationContext, IActivePivotConfig activePivotConfig,
                                        IHttpSecurityProcessor httpSecurityConsumer) {
        this.applicationContext = applicationContext;
        this.activePivotConfig = activePivotConfig;
        this.httpSecurityConsumer = httpSecurityConsumer;
    }

    @Bean
    @Order(1)
    public SecurityFilterChain activeUIFilterChain(HttpSecurity http) throws Exception {
        // Permit all on ActiveUI resources and the root (/) that redirects to ActiveUI index.html.
        final String pattern = "^(.{0}|\\/|\\/" + ActiveUIResourceServerConfig.UI_NAMESPACE + "(\\/.*)?)$";
        http
                // Only theses URLs must be handled by this HttpSecurity
                .regexMatcher(pattern)
                .authorizeRequests()
                // The order of the matchers matters
                .regexMatchers(HttpMethod.OPTIONS, pattern).permitAll()
                .regexMatchers(HttpMethod.GET, pattern).permitAll();

        // Authorizing pages to be embedded in iframes to have ActiveUI in ActiveMonitor UI
        return http.headers().frameOptions().disable()
                .and().build();
    }

    @Bean
    @Order(2)
    // Must be done before activePivotFilterChain (because they match common URLs)
    public SecurityFilterChain jwtFilterChain(HttpSecurity http) throws Exception {
        AuthenticationEntryPoint basicAuthenticationEntryPoint =
                applicationContext.getBean(BASIC_AUTH_BEAN_NAME, AuthenticationEntryPoint.class);
        return http.antMatcher(JwtRestServiceConfig.REST_API_URL_PREFIX + "/**")
                // As of Spring Security 4.0, CSRF protection is enabled by default.
                .csrf().disable()
                .cors().and()
                .authorizeRequests()
                .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .antMatchers("/**").hasAnyAuthority(ROLE_USER)
                .and()
                .httpBasic().authenticationEntryPoint(basicAuthenticationEntryPoint)
                .and().build();
    }

    @Bean
    @Order(3)
    // Must be done before activePivotFilterChain (because they match common URLs)
    public SecurityFilterChain versionsFilterChain(HttpSecurity http) throws Exception {
        return http.antMatcher(VersionServicesConfig.REST_API_URL_PREFIX + "/**")
                // As of Spring Security 4.0, CSRF protection is enabled by default.
                .csrf().disable()
                .cors().and()
                .authorizeRequests()
                .antMatchers("/**").permitAll()
                .and().build();
    }

    @Bean
    @Order(4)
    public SecurityFilterChain helloFilterChain(HttpSecurity http) throws Exception {
        return http.antMatcher("/hello")
                .csrf().disable()
                .cors().and()
                .authorizeRequests()
                .antMatchers("/hello")
                .hasAuthority(ROLE_TECH)
                .and()
                .httpBasic()
                .and().build();
    }

    @Bean
    @Order(100)
    public SecurityFilterChain activePivotFilterChain(HttpSecurity http) throws Exception {
        httpSecurityConsumer.preProcess(http);

        http.authorizeRequests()
                // The order of the matchers matters
                .antMatchers(HttpMethod.OPTIONS, REST_API_URL_PREFIX + "/**")
                .permitAll()
                // Web services used by AP live 3.4
                .antMatchers(CXF_WEB_SERVICES + '/' + ID_GENERATOR_SERVICE + "/**")
                .hasAnyAuthority(ROLE_USER)
                .antMatchers(CXF_WEB_SERVICES + '/' + LONG_POLLING_SERVICE + "/**")
                .hasAnyAuthority(ROLE_USER)
                .antMatchers(CXF_WEB_SERVICES + '/' + LICENSING_SERVICE + "/**")
                .hasAnyAuthority(ROLE_USER)
                // Spring remoting services used by AP live 3.4
                .antMatchers(url(ID_GENERATOR_REMOTING_SERVICE, "**"))
                .hasAnyAuthority(ROLE_USER)
                .antMatchers(url(LONG_POLLING_REMOTING_SERVICE, "**"))
                .hasAnyAuthority(ROLE_USER)
                .antMatchers(url(LICENSING_REMOTING_SERVICE, "**"))
                .hasAnyAuthority(ROLE_USER)
                // The ping service is temporarily authenticated (see PIVOT-3149)
                .antMatchers(url(REST_API_URL_PREFIX, PING_SUFFIX))
                .hasAnyAuthority(ROLE_USER)
                // REST services
                .antMatchers(REST_API_URL_PREFIX + "/**")
                .hasAnyAuthority(ROLE_USER)
                // One has to be a user for all the other URLs
                .antMatchers("/**")
                .hasAuthority(ROLE_USER)
                // SwitchUserFilter is the last filter in the chain. See FilterComparator class.
                .and()
                .addFilterAfter(this.activePivotConfig.contextValueFilter(), SwitchUserFilter.class);

        httpSecurityConsumer.postProcess(http);

        return http.build();
    }

}
