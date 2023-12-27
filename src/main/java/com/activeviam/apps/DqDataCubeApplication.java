/*
 * Copyright (C) ActiveViam 2023
 * ALL RIGHTS RESERVED. This material is the CONFIDENTIAL and PROPRIETARY
 * property of ActiveViam Limited. Any unauthorized use,
 * reproduction or transfer of this material is strictly prohibited
 */

package com.activeviam.apps;

import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.activeviam.apps.annotations.DirectQueryActivePivotApplication;

/**
 * COMMENT ME.
 *
 * @author ActiveViam
 */
@DirectQueryActivePivotApplication
@EnableWebMvc
@Profile("snowflake")
public class DqDataCubeApplication {
    public static void main(String[] args) {
        System.setProperty("spring.profiles.default", "snowflake,data1");
        System.setProperty("server.port", "9091");
        SpringApplication app = new SpringApplication(DqDataCubeApplication.class);
        app.run(args);
    }
}
