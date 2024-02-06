/*
 * Copyright (C) ActiveViam 2023-2024
 * ALL RIGHTS RESERVED. This material is the CONFIDENTIAL and PROPRIETARY
 * property of ActiveViam Limited. Any unauthorized use,
 * reproduction or transfer of this material is strictly prohibited
 */
package com.activeviam.apps;

import org.springframework.boot.SpringApplication;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.activeviam.apps.annotations.ActivePivotApplication;

@ActivePivotApplication
@EnableWebMvc
public class PivotSpringBootApplication {

    public static void main(String[] args) {
        SpringApplication.run(PivotSpringBootApplication.class, args);
    }
}
