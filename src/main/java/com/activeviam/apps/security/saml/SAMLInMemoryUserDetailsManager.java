package com.activeviam.apps.security.saml;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.saml.SAMLCredential;
import org.springframework.security.saml.userdetails.SAMLUserDetailsService;

import java.util.Collection;
import java.util.Properties;

/**
 * Extension of {@link InMemoryUserDetailsManager} which also implements {@link SAMLUserDetailsService}.
 *
 * This class allows for a basic working configuration. The username is taken from the NameID of the
 * {@link SAMLCredential}; actual implementations may take the username and authorities from elsewhere (e.g. custom
 * attributes in the credential).
 */
public class SAMLInMemoryUserDetailsManager extends InMemoryUserDetailsManager implements SAMLUserDetailsService {

    public SAMLInMemoryUserDetailsManager() {
    }

    public SAMLInMemoryUserDetailsManager(Collection<UserDetails> users) {
        super(users);
    }

    public SAMLInMemoryUserDetailsManager(UserDetails... users) {
        super(users);
    }

    public SAMLInMemoryUserDetailsManager(Properties users) {
        super(users);
    }

    @Override
    public Object loadUserBySAML(SAMLCredential credential) throws UsernameNotFoundException {
        return loadUserByUsername(credential.getNameID().getValue());
    }

}
