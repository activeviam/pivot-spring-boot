package com.activeviam.training.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author ActiveViam
 */
@Configuration
public class ActivePivotWebMvcConfigurer implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/content/ui/env*.js")
                .addResourceLocations("classpath:/static/content/");
        registry.addResourceHandler("/ui/env*.js")
                .addResourceLocations("classpath:/static/activeui/");

        registerExtensions(registry);

        registry.setOrder(Ordered.HIGHEST_PRECEDENCE);
    }

    protected void registerExtensions(final ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/ui/extensions*.json")
                .addResourceLocations("classpath:/static/activeui/");
        registry.addResourceHandler("/ui/extensions/mdx-drawer-extension/**/*.js")
                .addResourceLocations("classpath:/static/activeui/extensions/mdx-drawer-extension/");
    }

}
