package com.activeviam.apps.pivotspringboot;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
public class HelloController {

    @RequestMapping("/hello")
    public String index() {
        return "Hello from ActivePivotSpringBoot!";
    }
}