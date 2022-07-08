package com.activeviam.apps.cfg.security;

import com.qfs.content.service.IContentService;
import com.qfs.jwt.service.IJwtService;
import com.qfs.security.spring.impl.CompositeUserDetailsService;
import com.quartetfs.biz.pivot.security.IAuthorityComparator;
import com.quartetfs.biz.pivot.security.impl.AuthorityComparatorAdapter;
import com.quartetfs.fwk.ordering.impl.CustomComparator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import java.util.Arrays;
import java.util.List;

public class UserDetailsConfig {

    public static final String USER_DETAILS_SERVICE_QUALIFIER = "userDetailsService";

    public static final String ROLE_TECH = "ROLE_TECH";
    public static final String ROLE_ADMIN = "ROLE_ADMIN";
    public static final String ROLE_CS_ROOT = IContentService.ROLE_ROOT;
    public static final String ROLE_USER = "ROLE_USER";

    public static final String USER_TECH = "tech";
    public static final String PASSWORD_TECH = "tech";
    public static final String USER_ADMIN = "admin";
    public static final String PASSWORD_ADMIN = "admin";

    @Bean
    @Primary
    public UserDetailsService userDetailsService(
            @Autowired @Qualifier(USER_DETAILS_SERVICE_QUALIFIER) List<UserDetailsService> userDetailsServices) {
        return new CompositeUserDetailsService(userDetailsServices);
    }

    @Bean
    public AuthenticationProvider technicalAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(technicalUserDetailsService());
        return provider;
    }

    @Bean
    @Qualifier("userDetailsService")
    public UserDetailsService technicalUserDetailsService() {
        UserDetails user = User.withDefaultPasswordEncoder()
                .username(USER_TECH)
                .password(PASSWORD_TECH)
                .authorities(ROLE_TECH)
                .build();
        return new InMemoryUserDetailsManager(user);
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

}
