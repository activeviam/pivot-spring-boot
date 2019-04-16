package com.activeviam.apps.pivotspringboot;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletRegistrationBean;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.MultipartConfigElement;
import java.io.File;

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

	/**
	 * Special beans to make the contentService work in SpringBoot
	 * https://github.com/activeviam/ps-pivot-springboot/issues/7
	 *
	 */
	@Bean
	public WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> webServerFactoryCustomizer() {
		return factory -> factory.setDocumentRoot(new File("src/main/webapp"));
	}


}