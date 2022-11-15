/*
 * (C) ActiveViam 2022
 * ALL RIGHTS RESERVED. This material is the CONFIDENTIAL and PROPRIETARY
 * property of ActiveViam. Any unauthorized use,
 * reproduction or transfer of this material is strictly prohibited
 */
package com.activeviam.apps.activepivot.admin;

import com.activeviam.apps.activepivot.ActivePivotPropertiesConstants;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Properties;

/**
 * @author ActiveViam
 */
@ConfigurationProperties(ActivePivotPropertiesConstants.AP_CONTENT_PROPERTIES_PREFIX)
@Data
public class ContentServerProperties {

	private Properties dataSource = new Properties();

	private ContentServerSecurityProperties security = new ContentServerSecurityProperties();

	@Data
	public static class ContentServerSecurityProperties {
		private String calculatedMemberRole = "ROLE_USER";
		private String kpiRole = "ROLE_USER";
		private int cacheEntitlementsTTL = 3600;
	}

}
