package com.sbr.pivotspringboot;

import com.quartetfs.biz.xmla.servlet.pivot.impl.ActivePivotXmlaServlet;
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
     * Special beans to make AP work in SpringBoot
     *
     */

    /**
     * Register the CXF servlet
     */
    @Bean
    public ServletRegistrationBean cxfServletRegistrationBean() {
        return new ServletRegistrationBean(new CXFServlet(), "/*");
    }

    /**
     * Register the XMLA servlet
     */
    @Bean
    public ServletRegistrationBean xmlaServletRegistrationBean() {
        return new ServletRegistrationBean(new ActivePivotXmlaServlet(), "/xmla/*");
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

}