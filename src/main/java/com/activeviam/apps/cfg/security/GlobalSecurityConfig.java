package com.activeviam.apps.cfg.security;

import com.qfs.jwt.service.IJwtService;
import com.qfs.security.spring.impl.CompositeUserDetailsService;
import com.quartetfs.biz.pivot.security.IAuthorityComparator;
import com.quartetfs.biz.pivot.security.impl.AuthorityComparatorAdapter;
import com.quartetfs.biz.pivot.security.impl.UserDetailsServiceWrapper;
import com.quartetfs.fwk.ordering.impl.CustomComparator;
import com.quartetfs.fwk.security.IUserDetailsService;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.authentication.configuration.EnableGlobalAuthentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.UserDetailsManager;

@EnableGlobalAuthentication
@Configuration
@RequiredArgsConstructor
@Slf4j
public class GlobalSecurityConfig {
    private final UserDetailsManager technicalUserDetailsService;
    private final UserDetailsManager inMemoryUserDetailsService;

    @Bean
    public IUserDetailsService avUserDetailsService() {
        return new UserDetailsServiceWrapper(userDetailsService());
    }

    @Bean
    @Primary
    public UserDetailsService userDetailsService() {
        return new CompositeUserDetailsService(Arrays.asList(technicalUserDetailsService, inMemoryUserDetailsService));
    }

    /**
     * NOTE: JWT authentication provider bean is provided by JwtConfig
     */
    @Bean
    public AuthenticationManager globalAuthenticationManager(List<AuthenticationProvider> authenticationProviders) {
        var authenticationManager = new ProviderManager(authenticationProviders);
        authenticationManager.setEraseCredentialsAfterAuthentication(false);
        return authenticationManager;
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
        var comp = new CustomComparator<String>();
        comp.setLastObjects(List.of(SecurityConstants.ROLE_USER));
        comp.setLastObjects(List.of(SecurityConstants.ROLE_ADMIN));
        return new AuthorityComparatorAdapter(comp);
    }
}