/*
 * (C) ActiveViam 2022
 * ALL RIGHTS RESERVED. This material is the CONFIDENTIAL and PROPRIETARY
 * property of ActiveViam. Any unauthorized use,
 * reproduction or transfer of this material is strictly prohibited
 */
package com.activeviam.apps.activepivot.data.source;

import com.qfs.msg.csv.impl.CSVSourceConfiguration;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.nio.file.Path;

/**
 * @author ActiveViam
 */
@ConfigurationProperties
@Data
public class CsvSourceProperties {

	private int parserThreads = Math.max(2, Math.min(Runtime.getRuntime().availableProcessors() / 2, 8));

	private int bufferSize = 64;

	private boolean synchronousMode = false;

	public CSVSourceConfiguration.CSVSourceConfigurationBuilder<Path> toProperties() {
		CSVSourceConfiguration.CSVSourceConfigurationBuilder<Path> sourceConfigurationBuilder = new CSVSourceConfiguration.CSVSourceConfigurationBuilder<>();
		sourceConfigurationBuilder.parserThreads(getParserThreads());
		sourceConfigurationBuilder.synchronousMode(Boolean.valueOf(Boolean.toString(isSynchronousMode())));
		sourceConfigurationBuilder.bufferSize(getBufferSize());
		return sourceConfigurationBuilder;
	}

}
