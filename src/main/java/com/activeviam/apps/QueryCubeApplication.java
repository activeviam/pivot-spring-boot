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

@DatastoreActivePivotApplication
@EnableWebMvc
@Profile("query")
public class QueryCubeApplication {

    public static void main(String[] args) {
        System.setProperty("spring.profiles.default", "query");
        System.setProperty("server.port", "9090");
        SpringApplication.run(QueryCubeApplication.class, args);
    }

}
