/*
 * (C) ActiveViam 2016-2020
 * ALL RIGHTS RESERVED. This material is the CONFIDENTIAL and PROPRIETARY
 * property of ActiveViam. Any unauthorized use,
 * reproduction or transfer of this material is strictly prohibited
 */

package com.activeviam.apps.cfg;

import com.qfs.server.cfg.impl.ASpringResourceServerConfig;
import com.qfs.util.impl.QfsArrays;
import java.util.Set;
import org.springframework.context.annotation.Configuration;

/**
 * Spring configuration for ActiveUI web application
 *
 * @author ActiveViam
 */
@Configuration
public class ActiveUIResourceServerConfig extends ASpringResourceServerConfig {

    /** The namespace of the ActiveUI web application */
    public static final String UI_NAMESPACE = "ui";

    /** Constructor */
    public ActiveUIResourceServerConfig() {
        super("/" + UI_NAMESPACE);
    }

    @Override
    protected void registerRedirections(final ResourceRegistry registry) {
        super.registerRedirections(registry);
        // Redirect from the root to ActiveUI
        registry.redirectTo(UI_NAMESPACE + "/index.html", "/");
        registry.serve("/content/ui/env*.js").addResourceLocations("classpath:/static/content/");
        registry.serve("/ui/env*.js").addResourceLocations("classpath:/static/activeui/");
        registerExtensions(registry);

    }

    protected void registerExtensions(final ResourceRegistry registry) {
        registry.serve("/ui/extensions*.json")
            .addResourceLocations("classpath:/static/activeui/");
        registry.serve("/ui/extensions/mdx-drawer-extension/**/*.js")
            .addResourceLocations("classpath:/static/activeui/extensions/mdx-drawer-extension/");
    }


    /**
     * Registers resources to serve.
     *
     * @param registry registry to use
     */
    @Override
    protected void registerResources(final ResourceRegistry registry) {
        super.registerResources(registry);

        // ActiveUI web app also serves request to the root, so that the redirection from root to ActiveUI works
        registry.serve("/")
                .addResourceLocations(
                        "/",
                        "classpath:META-INF/resources/")
                .setCacheControl(getDefaultCacheControl());
    }

    /**
     * Gets the extensions of files to serve.
     * @return all files extensions
     */
    @Override
    public Set<String> getServedExtensions() {
        return QfsArrays.mutableSet(
                // Default HTML files
                "html", "js", "css", "map", "json",
                // Image extensions
                "png", "jpg", "gif", "ico",
                // Font extensions
                "eot", "svg", "ttf", "woff", "woff2"
        );
    }

    @Override
    public Set<String> getServedDirectories() {
        return QfsArrays.mutableSet("/");
    }

    @Override
    public Set<String> getResourceLocations() {
        // ActiveUI is integrated in the sandbox project thanks to Maven integration.
        // You can read more about this feature here https://support.activeviam.com/documentation/activeui/4.2.0/dev/setup/maven-integration.html

        return QfsArrays.mutableSet(
                "/activeui/", // index.html, favicon.ico, etc.
                "classpath:META-INF/resources/webjars/activeui/"); // ActiveUI SDK UMD scripts and supporting assets
    }

}
