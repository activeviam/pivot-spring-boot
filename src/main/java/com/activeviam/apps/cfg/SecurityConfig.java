package com.activeviam.apps.cfg;

import com.qfs.content.service.IContentService;
import com.qfs.jwt.impl.JwtFilter;
import com.qfs.jwt.service.IJwtService;
import com.qfs.server.cfg.IActivePivotConfig;
import com.qfs.server.cfg.IJwtConfig;
import com.qfs.servlet.handlers.impl.NoRedirectLogoutSuccessHandler;
import com.quartetfs.biz.pivot.security.IAuthorityComparator;
import com.quartetfs.biz.pivot.security.impl.AuthorityComparatorAdapter;
import com.quartetfs.fwk.ordering.impl.CustomComparator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.EnableGlobalAuthentication;
import org.springframework.security.config.annotation.authentication.configurers.provisioning.InMemoryUserDetailsManagerConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.switchuser.SwitchUserFilter;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

import static com.qfs.QfsWebUtils.url;
import static com.qfs.server.cfg.impl.ActivePivotRestServicesConfig.PING_SUFFIX;
import static com.qfs.server.cfg.impl.ActivePivotRestServicesConfig.REST_API_URL_PREFIX;

@EnableGlobalAuthentication
@EnableWebSecurity( debug = false )
@Configuration
public class SecurityConfig {

    /** Admin role */
    public static final String ROLE_ADMIN = "ROLE_ADMIN";
    public static final String ROLE_USER  ="ROLE_USER";
    /** Content Server Root role */
    public static final String ROLE_CS_ROOT = IContentService.ROLE_ROOT;

    public static final String ROLE_APAC = "ROLE_APAC";

    /** Admin user */
    public static final String USER_ADMIN = "admin";
    public static final String PASSWORD_ADMIN = "admin";

    /** Cookie name for NanoPivot */
    public static final String COOKIE_NAME = "NP_JSESSIONID";

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

    @Autowired
    public void configureGlobal(final AuthenticationManagerBuilder auth) throws Exception {
        auth.eraseCredentials(false)
                .authenticationProvider(jwtConfig.jwtAuthenticationProvider());
    }

    /**
     * Common web security configuration for {@link HttpSecurity}.
     *
     * @author ActiveViam
     */
    public static abstract class AWebSecurityConfigurer extends WebSecurityConfigurerAdapter {

        /** {@code true} to enable the logout URL */
        protected final boolean logout;
        /** The name of the cookie to clear */
        protected final String cookieName;

        @Autowired
        protected ApplicationContext context;

        /**
         * This constructor does not enable the logout URL
         */
        public AWebSecurityConfigurer() {
            this(null);
        }

        /**
         * This constructor enables the logout URL
         *
         * @param cookieName
         *            the name of the cookie to clear
         */
        public AWebSecurityConfigurer(final String cookieName) {
            this.logout = cookieName != null;
            this.cookieName = cookieName;
        }

        @Override
        protected final void configure(final HttpSecurity http) throws Exception {
            final JwtFilter jwtFilter = context.getBean(JwtFilter.class);

            http
                    // As of Spring Security 4.0, CSRF protection is enabled by default.
                    .csrf().disable()
                    .cors().and()
                    // To Allow authentication with JW ( Needed for Active UI )
                    .addFilterAfter(jwtFilter, CorsFilter.class);

            if (logout) {
                // Configure logout URL
                http.logout()
                        .permitAll()
                        .deleteCookies(cookieName)
                        .invalidateHttpSession(true)
                        .logoutSuccessHandler(new NoRedirectLogoutSuccessHandler());
            }

            doConfigure(http);

        }

        /**
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
