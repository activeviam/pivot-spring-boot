package com.activeviam.apps.security;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;

public interface IHttpSecurityProcessor {

    default void preProcess(HttpSecurity http) throws Exception {
        // By default, do nothing
    }

    default void postProcess(HttpSecurity http) throws Exception {
        // By default, do nothing
    }

}
