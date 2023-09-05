package com.activeviam.apps.cfg.security;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(TechnicalUsersProperties.TECH_USERS_PROPERTIES_PREFIX)
@Data
public class TechnicalUsersProperties {
    public static final String TECH_USERS_PROPERTIES_PREFIX = "tech-users";
    public static final String PIVOT_USERNAME = "pivot";
    public static final String MONITOR_USERNAME = "monitor";

    private String pivot;
    private String monitor;
}