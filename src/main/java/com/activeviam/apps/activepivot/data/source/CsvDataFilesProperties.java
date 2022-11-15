/*
 * (C) ActiveViam 2022
 * ALL RIGHTS RESERVED. This material is the CONFIDENTIAL and PROPRIETARY
 * property of ActiveViam. Any unauthorized use,
 * reproduction or transfer of this material is strictly prohibited
 */
package com.activeviam.apps.activepivot.data.source;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

import static com.activeviam.apps.activepivot.ActivePivotPropertiesConstants.AP_DATA_PROPERTIES_PREFIX;

/**
 * @author ActiveViam
 */
@ConfigurationProperties(AP_DATA_PROPERTIES_PREFIX)
@Data
public class CsvDataFilesProperties {

	private Map<String, String> files;

	private boolean headerRow = true;

	private CsvSourceProperties csvSourceProperties = new CsvSourceProperties();

}
