/*
 * (C) ActiveViam 2019
 * ALL RIGHTS RESERVED. This material is the CONFIDENTIAL and PROPRIETARY
 * property of ActiveViam. Any unauthorized use,
 * reproduction or transfer of this material is strictly prohibited
 */
package com.activeviam.apps.pivotspringboot.activepivot;

import com.qfs.server.cfg.impl.ActivePivotWebSocketServicesConfig;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @author ActiveViam
 */
@Configuration
public class CustomWebSocketServicesConfig extends ActivePivotWebSocketServicesConfig {

	// This method would normally get a bean of type ICorsFilterConfig to then get the allowed origins.
	// We are not using ICorsFilterConfig (ACorsFilterConfig) anymore, so we need to provide the allowed origins in a different way
	@Override
	protected String[] getAllowedOrigins() {
		final List<String> origins = CustomCorsConfiguration.getAllowedOrigins();
		return origins.toArray(new String[origins.size()]);
	}
}
