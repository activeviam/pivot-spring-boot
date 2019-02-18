package com.sbr.pivotspringboot;

import org.apache.cxf.transport.servlet.CXFServlet;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class PivotSpringbootApplication {

	public static void main(String[] args) {
		SpringApplication.run(PivotSpringbootApplication.class, args);
	}

	/**
	 * Apparently mandatory to allow calls as raised on github
	 * https://github.com/spring-projects/spring-boot/issues/15373
	 *
	 * However appears it to work without it?!?!
	 */
//	@Bean
//	public DispatcherServletRegistrationBean dispatcherServletRegistration(
//			DispatcherServlet dispatcherServlet,
//			ObjectProvider<MultipartConfigElement> multipartConfig) {
//		DispatcherServletRegistrationBean registration = new DispatcherServletRegistrationBean(
//				dispatcherServlet, "/*");
//		registration.setName("springDispatcherServlet");
//		registration.setLoadOnStartup(1);
//		multipartConfig.ifAvailable(registration::setMultipartConfig);
//		return registration;
//	}

	/**
	 * Register the CXF servlet
	 */
	@Bean
	public ServletRegistrationBean servletRegistrationBean(){
		return new ServletRegistrationBean(new CXFServlet(),"/api/*");
	}

}