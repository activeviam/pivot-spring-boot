package com.activeviam.apps;

import com.activeviam.apps.annotations.ActivePivotApplication;
import com.activeviam.apps.cfg.PivotConfig;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.MultipartConfigElement;

@ActivePivotApplication
@Import(PivotConfig.class)
public class PivotSpringBootApplication {

    public static void main(String[] args) {
        SpringApplication.run(PivotSpringBootApplication.class, args);
    }

    /**
     * Special beans to make AP work in SpringBoot
     * https://github.com/spring-projects/spring-boot/issues/15373
     */
    @Bean
    public DispatcherServletRegistrationBean dispatcherServletRegistration(
            DispatcherServlet dispatcherServlet,
            ObjectProvider<MultipartConfigElement> multipartConfig) {
        DispatcherServletRegistrationBean registration = new DispatcherServletRegistrationBean(
                dispatcherServlet, "/*");
        registration.setName("springDispatcherServlet");
        registration.setLoadOnStartup(1);
        multipartConfig.ifAvailable(registration::setMultipartConfig);
        return registration;
    }
}
