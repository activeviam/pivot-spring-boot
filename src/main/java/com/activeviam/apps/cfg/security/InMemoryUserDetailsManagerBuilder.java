package com.activeviam.apps.cfg.security;

import com.activeviam.fwk.ActiveViamRuntimeException;
import java.util.ArrayList;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.provisioning.InMemoryUserDetailsManagerConfigurer;
import org.springframework.security.config.annotation.authentication.configurers.provisioning.UserDetailsManagerConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;

/**
 * An In-memory {@link UserDetailsService} builder which can be used without
 * {@link AuthenticationManagerBuilder} contrary to {@link InMemoryUserDetailsManagerConfigurer}.
 *
 * @author ActiveViam
 */
public class InMemoryUserDetailsManagerBuilder
        extends UserDetailsManagerConfigurer<AuthenticationManagerBuilder, InMemoryUserDetailsManagerBuilder> {

    /**
     * Creates a new instance
     */
    public InMemoryUserDetailsManagerBuilder() {
        super(new InMemoryUserDetailsManager(new ArrayList<>()));
    }

    @Override
    public void configure(AuthenticationManagerBuilder builder) throws Exception {
        if (builder != null) {
            throw new IllegalArgumentException();
        }
        initUserDetailsService();
    }

    /**
     * Builds the In-memory {@link UserDetailsManager} and returns it
     *
     * @return the built object
     */
    public UserDetailsManager build() {
        try {
            configure(null);
            return getUserDetailsService();
        } catch (Exception e) {
            throw new ActiveViamRuntimeException(e);
        }
    }
}