package com.activeviam.apps.cfg.security;

import java.time.Duration;
import java.util.Collections;
import java.util.Map;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.lang.NonNull;

@ConfigurationProperties(SecurityJwtProperties.JWT_PROPERTIES_PREFIX)
@Data
public class SecurityJwtProperties {
    public static final String JWT_PROPERTIES_PREFIX = "activeviam.jwt";
    private static final String PUBLIC_KEY = "public";
    private static final String PRIVATE_KEY = "private";

    @NonNull
    @Getter(AccessLevel.PRIVATE)
    private Map<String, String> key = Collections.emptyMap();

    private Duration expiration = Duration.ofHours(12);
    private boolean configureLogout = true;
    private boolean failOnDifferentAuthorities;

    public String getPublicKey() {
        return key.get(PUBLIC_KEY);
    }

    public String getPrivateKey() {
        return key.get(PRIVATE_KEY);
    }
}