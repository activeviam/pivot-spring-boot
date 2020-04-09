package com.activeviam.apps.cfg;

import com.activeviam.security.cfg.ICorsConfig;
import com.qfs.content.service.IContentService;
import com.qfs.jwt.service.IJwtService;
import com.qfs.pivot.servlet.impl.ContextValueFilter;
import com.qfs.server.cfg.IActivePivotConfig;
import com.qfs.server.cfg.IJwtConfig;
import com.qfs.servlet.handlers.impl.NoRedirectLogoutSuccessHandler;
import com.quartetfs.biz.pivot.security.IAuthorityComparator;
import com.quartetfs.biz.pivot.security.impl.AuthorityComparatorAdapter;
import com.quartetfs.fwk.ordering.impl.CustomComparator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.EnableGlobalAuthentication;
import org.springframework.security.config.annotation.authentication.configurers.provisioning.InMemoryUserDetailsManagerConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.switchuser.SwitchUserFilter;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;
import org.springframework.security.web.firewall.StrictHttpFirewall;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.servlet.Filter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.qfs.QfsWebUtils.url;
import static com.qfs.server.cfg.impl.ActivePivotRestServicesConfig.PING_SUFFIX;
import static com.qfs.server.cfg.impl.ActivePivotRestServicesConfig.REST_API_URL_PREFIX;

@EnableGlobalAuthentication
@EnableWebSecurity( debug = false )
@Configuration
public class SecurityConfig implements ICorsConfig {

    /** Set to true to allow anonymous access. */
    public static final boolean useAnonymous = false;

    public static final String BASIC_AUTH_BEAN_NAME = "basicAuthenticationEntryPoint";

    /** Admin role */
    public static final String ROLE_ADMIN = "ROLE_ADMIN";
    public static final String ROLE_USER  ="ROLE_USER";
    /** Content Server Root role */
    public static final String ROLE_CS_ROOT = IContentService.ROLE_ROOT;

    public static final String ROLE_APAC = "ROLE_APAC";

    /** Admin user */
    public static final String USER_ADMIN = "admin";
    public static final String PASSWORD_ADMIN = "admin";

    /** Cookie name for AP */
    public static final String COOKIE_NAME = "AP_JSESSIONID";

    @Autowired
    protected IJwtConfig jwtConfig;

    @Autowired
    protected AuthenticationManagerBuilder auth;

    @Bean
    public PasswordEncoder passwordEncoder() {
        PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        ((DelegatingPasswordEncoder) passwordEncoder).setDefaultPasswordEncoderForMatches(NoOpPasswordEncoder.getInstance());
        return passwordEncoder;
    }


    // UserDetailsService Bean implemented as it is needed by jwtAuthenticationProvider to enable connection through jwt
    @Bean
    public UserDetailsService userDetailsService() throws Exception {

        InMemoryUserDetailsManagerConfigurer<AuthenticationManagerBuilder> inMemUserDetailsManagerConfig =
                new InMemoryUserDetailsManagerConfigurer<AuthenticationManagerBuilder>()
                        .passwordEncoder(passwordEncoder())
                        .withUser(USER_ADMIN).password(PASSWORD_ADMIN).authorities(ROLE_ADMIN,ROLE_CS_ROOT,ROLE_USER)
                        .and()
                ;

        inMemUserDetailsManagerConfig.configure(auth);

        UserDetailsService userDetailSrc = inMemUserDetailsManagerConfig.getUserDetailsService();

        return userDetailSrc;
    }

    /**
     * Returns the default {@link AuthenticationEntryPoint} to use
     * for the fallback basic HTTP authentication.
     *
     * @return The default {@link AuthenticationEntryPoint} for the
     *         fallback HTTP basic authentication.
     */
    @Bean(name = BASIC_AUTH_BEAN_NAME)
    public AuthenticationEntryPoint basicAuthenticationEntryPoint() {
        return new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED);
    }

    /**
     * [Bean] Comparator for user roles
     * <p>
     * Defines the comparator used by:
     * </p>
     * <ul>
     *   <li>com.quartetfs.biz.pivot.security.impl.ContextValueManager#setAuthorityComparator(IAuthorityComparator)</li>
     *   <li>{@link IJwtService}</li>
     * </ul>
     * @return a comparator that indicates which authority/role prevails over another. <b>NOTICE -
     *         an authority coming AFTER another one prevails over this "previous" authority.</b>
     *         This authority ordering definition is essential to resolve possible ambiguity when,
     *         for a given user, a context value has been defined in more than one authority
     *         applicable to that user. In such case, it is what has been set for the "prevailing"
     *         authority that will be effectively retained for that context value for that user.
     */
    @Bean
    public IAuthorityComparator authorityComparator() {
        final CustomComparator<String> comp = new CustomComparator<>();
        comp.setFirstObjects(Arrays.asList(ROLE_CS_ROOT));
        comp.setLastObjects(Arrays.asList(ROLE_ADMIN));
        comp.setLastObjects(Arrays.asList(ROLE_USER));
        return new AuthorityComparatorAdapter(comp);
    }

    /**
     * Configures the authentication of the whole application.
     *
     * <p>This binds the defined user service to the authentication and sets the source
     * for JWT tokens.
     *
     *  @param auth Spring builder to manage authentication
     * @throws Exception in case of error
     */
    @Autowired
    public void configureGlobal(final AuthenticationManagerBuilder auth) throws Exception {
        auth
                .eraseCredentials(false)
                // Add an LDAP authentication provider instead of this to support LDAP
                .userDetailsService(userDetailsService()).and()
                // Required to allow JWT
                .authenticationProvider(jwtConfig.jwtAuthenticationProvider());
    }


//    @Bean
//    public FilterRegistrationBean<ContextValueFilter> disableRegisteringContextValueFilter(final ContextValueFilter filter) {
//        final FilterRegistrationBean<ContextValueFilter> registration = new FilterRegistrationBean<>(filter);
//        registration.setEnabled(false);
//        return registration;
//    }

    @Override
    public List<String> getAllowedOrigins() {
        return Collections.singletonList(CorsConfiguration.ALL);
    }

    /**
     * [Bean] Spring standard way of configuring CORS.
     *
     * <p>This simply forwards the configuration of {@link ICorsConfig} to Spring security system.
     *
     * @return the configuration for the application.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        final CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(getAllowedOrigins());
        configuration.setAllowedHeaders(getAllowedHeaders());
        configuration.setExposedHeaders(getExposedHeaders());
        configuration.setAllowedMethods(getAllowedMethods());
        configuration.setAllowCredentials(true);

        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    /**
     * Common configuration for {@link HttpSecurity}.
     *
     * @author ActiveViam
     */
    public abstract static class AWebSecurityConfigurer extends WebSecurityConfigurerAdapter {

        /**
         * {@code true} to enable the logout URL.
         */
        protected final boolean logout;
        /** The name of the cookie to clear. */
        protected final String cookieName;

        @Autowired
        protected Environment env;

        @Autowired
        protected ApplicationContext context;

        /**
         * This constructor does not enable the logout URL.
         */
        public AWebSecurityConfigurer() {
            this(null);
        }

        /**
         * This constructor enables the logout URL.
         *
         * @param cookieName the name of the cookie to clear
         */
        public AWebSecurityConfigurer(String cookieName) {
            this.logout = cookieName != null;
            this.cookieName = cookieName;
        }

        /**
         * {@inheritDoc}
         * <p>This configures a new firewall accepting `%` in URLs, as none of the core services encode
         * information in URL. This prevents from double-decoding exploits.<br>
         * The firewall is also configured to accept `\` - backslash - as none of ActiveViam APIs offer
         * to manipulate files from URL parameters.<br>
         * Yet, nor `/` and `.` - slash and point - are accepted, as it may trick the REGEXP matchers
         * used for security. Support for those two characters can be added at your own risk, by
         * extending this method. As far as ActiveViam APIs are concerned, `/` and `.` in URL parameters
         * do not represent any risk. `;` - semi-colon - is also not supported, for various APIs end up
         * target an actual database, and because this character is less likely to be used.
         * </p>
         */
        @Override
        public void configure(WebSecurity web) throws Exception {
            super.configure(web);

            final StrictHttpFirewall firewall = new StrictHttpFirewall();
            firewall.setAllowUrlEncodedPercent(true);
            firewall.setAllowBackSlash(true);

            firewall.setAllowUrlEncodedSlash(false);
            firewall.setAllowUrlEncodedPeriod(false);
            firewall.setAllowSemicolon(false);
            web.httpFirewall(firewall);
        }

        @Override
        protected final void configure(final HttpSecurity http) throws Exception {
            final Filter jwtFilter = context.getBean(IJwtConfig.class).jwtFilter();

            http
                    // As of Spring Security 4.0, CSRF protection is enabled by default.
                    .csrf().disable()
                    .cors().and()
                    // To allow authentication with JWT (Required for ActiveUI)
                    .addFilterAfter(jwtFilter, SecurityContextPersistenceFilter.class);

            if (logout) {
                // Configure logout URL
                http.logout()
                        .permitAll()
                        .deleteCookies(cookieName)
                        .invalidateHttpSession(true)
                        .logoutSuccessHandler(new NoRedirectLogoutSuccessHandler());
            }

            if (useAnonymous) {
                // Handle anonymous users. The granted authority ROLE_USER
                // will be assigned to the anonymous request
                http.anonymous().principal("guest").authorities(ROLE_USER);
            }

            doConfigure(http);
        }

        /**
         * Applies the specific configuration for the endpoint.
         * @see #configure(HttpSecurity)
         */
        protected abstract void doConfigure(HttpSecurity http) throws Exception;
    }

    @Configuration
    @Order(2)
    // Must be done before ActivePivotSecurityConfigurer (because they match common URLs)
    public static class VersionsSecurityConfigurer extends AWebSecurityConfigurer {

        @Override
        protected void doConfigure(HttpSecurity http) throws Exception {
            String url = "/versions";
            http
                    .antMatcher(url + "/**").authorizeRequests()
                    .antMatchers(url + "/**").permitAll()
                    .and().httpBasic();
        }
    }

    @Configuration
    @Order(4)
// Must be done before ActivePivotSecurityConfigurer (because they match common URLs)
    public static class JWTSecurityConfigurer extends AWebSecurityConfigurer {

        @Override
        protected void doConfigure(HttpSecurity http) throws Exception {
            String url = "/jwt";
            http
                    .antMatcher(url + "/**").authorizeRequests()
                    .antMatchers(url + "/**").permitAll();
        }
    }

    /**
     * Configure security for ActivePivot web services
     *
     * @author ActiveViam
     *
     */
    @Configuration
    public static class ActivePivotSecurityConfigurer extends AWebSecurityConfigurer {

        @Autowired
        protected IActivePivotConfig activePivotConfig;

        /**
         * Constructor
         */
        public ActivePivotSecurityConfigurer() {
            super(COOKIE_NAME);
        }

        @Override
        protected void doConfigure(HttpSecurity http) throws Exception {
            http.authorizeRequests()
                    // The order of the matchers matters
                    // The REST ping service is temporarily authenticated (see PIVOT-3149)
                    .antMatchers(url(REST_API_URL_PREFIX, PING_SUFFIX)).hasAnyAuthority(ROLE_ADMIN)
                    // REST services
                    .antMatchers(REST_API_URL_PREFIX + "/**").hasAnyAuthority(ROLE_ADMIN)
                    // user admin can also have acess to all the other URLs
                    .antMatchers("/**").hasAuthority(ROLE_ADMIN)
                    .and().httpBasic()
                    // SwitchUserFilter is the last filter in the chain. See FilterComparator class.
                    .and()
                    .addFilterAfter(activePivotConfig.contextValueFilter(), SwitchUserFilter.class);
        }

        @Bean(name = BeanIds.AUTHENTICATION_MANAGER)
        @Override
        public AuthenticationManager authenticationManagerBean() throws Exception {
            return super.authenticationManagerBean();
        }

    }

}
