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

import com.activeviam.apps.annotations.DatastoreActivePivotApplication;
import com.activeviam.apps.annotations.DirectQueryActivePivotApplication;

@DirectQueryActivePivotApplication
@EnableWebMvc
@Profile("snowflake")
public class DirectQuerySpringBootApplication {

    public static void main(String[] args) {
        // Run with following JVM properties
        //--add-opens=java.base/java.lang=ALL-UNNAMED
        //--add-opens=java.base/java.nio=ALL-UNNAMED
        //--add-opens=java.base/sun.nio.ch=ALL-UNNAMED
        //--add-opens=java.base/java.util.concurrent=ALL-UNNAMED
        // -Dactiveviam.directquery.spring.enableLegacyInitialization=false

        System.setProperty("spring.profiles.default", "snowflake");
        System.setProperty("server.port", "9092");
        SpringApplication.run(DirectQuerySpringBootApplication.class, args);
    }
}
