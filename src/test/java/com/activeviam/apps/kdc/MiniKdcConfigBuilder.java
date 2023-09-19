/*
 * Copyright (C) ActiveViam 2023
 * ALL RIGHTS RESERVED. This material is the CONFIDENTIAL and PROPRIETARY
 * property of ActiveViam Limited. Any unauthorized use,
 * reproduction or transfer of this material is strictly prohibited
 */

package com.activeviam.apps.kdc;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

import org.apache.commons.io.FileUtils;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Singular;
import lombok.SneakyThrows;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@Builder(builderClassName = "Builder")
class MiniKdcConfig {
	private String workDir;
	private String confDir;
	private String keytabName;
	@Singular
	private Collection<String> principals;

	static class Builder {
		@SneakyThrows
		MiniKdcConfig build() {
			Path dir = Paths.get(workDir);
			File directory = dir.normalize().toFile();
			FileUtils.deleteQuietly(directory);
			FileUtils.forceMkdir(directory);
			this.workDir = dir.toString();

			try {
				URL resource = Thread.currentThread().getContextClassLoader().getResource(confDir);
				URI uri = Objects.requireNonNull(resource).toURI();
				this.confDir = Paths.get(uri).toString();
			} catch (URISyntaxException cause) {
				throw new IllegalStateException("Could not resolve path for: " + confDir, cause);
			}
			return new MiniKdcConfig(workDir, confDir, Paths.get(workDir).resolve(keytabName).toString(), principals);
		}
	}

	public String[] asConfig() {
		Collection<String> config = new ArrayList<>();
		config.add(workDir);
		config.add(confDir);
		config.add(keytabName);
		config.addAll(principals);
		return config.toArray(new String[0]);
	}
}