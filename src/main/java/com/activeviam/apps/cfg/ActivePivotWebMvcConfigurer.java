package com.activeviam.apps.cfg;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.config.annotation.*;

/**
 * @author ActiveViam
 */
@EnableWebMvc
@Configuration
public class ActivePivotWebMvcConfigurer implements WebMvcConfigurer {

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/content/ui/env*.js")
				.addResourceLocations("classpath:/static/content/");
		registry.addResourceHandler("/ui/env*.js")
				.addResourceLocations("classpath:/static/activeui/");
		registry.setOrder(Ordered.HIGHEST_PRECEDENCE);
	}

}
