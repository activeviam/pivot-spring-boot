/*
 * (C) ActiveViam 2022
 * ALL RIGHTS RESERVED. This material is the CONFIDENTIAL and PROPRIETARY
 * property of ActiveViam. Any unauthorized use,
 * reproduction or transfer of this material is strictly prohibited
 */
package com.activeviam.apps.activepivot.data.source;

import com.qfs.msg.csv.ICSVSourceConfiguration;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Properties;

/**
 * @author ActiveViam
 */
@ConfigurationProperties
@Data
public class CsvSourceProperties {

	private int parserThreads = Math.max(2, Math.min(Runtime.getRuntime().availableProcessors() / 2, 8));

	private int bufferSize = 64;

	private boolean synchronousMode = false;

	public Properties toProperties() {
		final var sourceProps = new Properties();
		sourceProps.put(ICSVSourceConfiguration.PARSER_THREAD_PROPERTY, Integer.toString(getParserThreads()));
		sourceProps.put(ICSVSourceConfiguration.SYNCHRONOUS_MODE_PROPERTY, Boolean.toString(isSynchronousMode()));
		sourceProps.put(ICSVSourceConfiguration.BUFFER_SIZE_PROPERTY, Integer.toString(getBufferSize()));
		return sourceProps;
	}

}
