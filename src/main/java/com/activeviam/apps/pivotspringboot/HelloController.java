package com.activeviam.apps.pivotspringboot;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
public class HelloController {

    @GetMapping("/hello")
    public String index() {
        return "Hello from ActivePivotSpringBoot!";
    }
}