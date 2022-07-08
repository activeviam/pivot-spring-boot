package com.activeviam.apps.cfg.security;

import com.activeviam.collections.impl.Immutable;
import com.activeviam.security.cfg.ICorsConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class CorsFilterConfig implements ICorsConfig {

    @Override
    public List<String> getAllowedOrigins() {
        return Immutable.list(CorsConfiguration.ALL).toList();
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
        configuration.setAllowedOriginPatterns(getAllowedOrigins());
        configuration.setAllowedHeaders(getAllowedHeaders());
        configuration.setExposedHeaders(getExposedHeaders());
        configuration.setAllowedMethods(getAllowedMethods());
        configuration.setAllowCredentials(true);

        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

}
