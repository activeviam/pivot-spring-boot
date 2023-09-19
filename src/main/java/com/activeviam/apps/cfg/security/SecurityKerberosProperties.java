/*
 * Copyright (C) ActiveViam 2023
 * ALL RIGHTS RESERVED. This material is the CONFIDENTIAL and PROPRIETARY
 * property of ActiveViam Limited. Any unauthorized use,
 * reproduction or transfer of this material is strictly prohibited
 */
package com.activeviam.apps.cfg.security;

import javax.validation.constraints.NotNull;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;
import org.springframework.validation.annotation.Validated;

import lombok.Data;

@ConfigurationProperties(prefix = SecurityKerberosProperties.KERBEROS_PROPERTIES_PREFIX)
@Data
@Validated
public class SecurityKerberosProperties {
    public static final String KERBEROS_PROPERTIES_PREFIX = "kerberos";
//    public static final String DEFAULT_KERBEROS_WORK_DIR = "krb-test-workdir";

//    private String workDir = DEFAULT_KERBEROS_WORK_DIR;
    @NotNull
    private String servicePrincipal;
    @NotNull
    private String userPrincipal;
    @NotNull
    private Resource keytabLocation;
    @NotNull
    private String accessUrl;
}
