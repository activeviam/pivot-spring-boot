package com.activeviam.apps.cfg.security;

import static com.activeviam.apps.cfg.security.TechnicalUsersProperties.MONITOR_USERNAME;
import static com.activeviam.apps.cfg.security.TechnicalUsersProperties.PIVOT_USERNAME;

import com.qfs.content.service.IContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;

@Configuration
@RequiredArgsConstructor
public class TechnicalAuthenticationSecurityConfig {

    private final PasswordEncoder passwordEncoder;
    private final TechnicalUsersProperties technicalUsers;

    @Bean
    public AuthenticationProvider technicalAuthenticationProvider() {
        var authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setPasswordEncoder(passwordEncoder);
        authenticationProvider.setUserDetailsService(technicalUserDetailsService());
        return authenticationProvider;
    }

    @Bean
    public UserDetailsManager technicalUserDetailsService() {
        var builder = new InMemoryUserDetailsManagerBuilder();
        // Technical user for ActivePivot server
        builder.passwordEncoder(passwordEncoder)
                .withUser(PIVOT_USERNAME)
                .password(passwordEncoder.encode(technicalUsers.getPivot()))
                .authorities(SecurityConstants.ROLE_TECH, IContentService.ROLE_ROOT)
                .and()
                .withUser(MONITOR_USERNAME)
                .password(passwordEncoder.encode(technicalUsers.getMonitor()))
                .authorities(SecurityConstants.ROLE_ACTUATOR);
        return builder.build();
    }
}