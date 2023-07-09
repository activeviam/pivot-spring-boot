package com.activeviam.apps;

import com.activeviam.apps.annotations.ActivePivotApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@ActivePivotApplication
@EnableWebMvc
public class PivotSpringBootApplication {

    public static void main(String[] args) {
        SpringApplication.run(PivotSpringBootApplication.class, args);
    }
}
