package com.activeviam.apps.pivotspringboot;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.MultipartConfigElement;

@SpringBootApplication
public class PivotSpringbootApplication {

    public static void main(String[] args) {
        SpringApplication.run(PivotSpringbootApplication.class, args);
    }

    /**
     * Special beans to make AP work in SpringBoot
     * https://github.com/spring-projects/spring-boot/issues/15373
     *
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