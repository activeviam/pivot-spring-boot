package com.activeviam.apps.cfg.security.saml;

import com.activeviam.apps.cfg.security.UserDetailsConfig;
import com.activeviam.apps.security.saml.SAMLInMemoryUserDetailsManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.saml.SAMLAuthenticationProvider;

@Configuration
public class SAMLAuthenticationProviderConfig {

    @Bean
    public SAMLAuthenticationProvider samlAuthenticationProvider() {
        SAMLAuthenticationProvider provider = new SAMLAuthenticationProvider();
        provider.setUserDetails(samlInMemoryUserDetailsManager());
        return provider;
    }

    @Bean
    @Qualifier(UserDetailsConfig.USER_DETAILS_SERVICE_QUALIFIER)
    public SAMLInMemoryUserDetailsManager samlInMemoryUserDetailsManager() {
        // TODO insert test users here
        UserDetails user = User.builder()
                .username("")
                // Password can be left empty as it isn't used by SAML
                .password("")
                .authorities(
                        UserDetailsConfig.ROLE_ADMIN,
                        UserDetailsConfig.ROLE_CS_ROOT,
                        UserDetailsConfig.ROLE_USER
                )
                .build();
        return new SAMLInMemoryUserDetailsManager(user);
    }

}
