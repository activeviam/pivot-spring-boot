/*
 * Copyright (C) ActiveViam 2023
 * ALL RIGHTS RESERVED. This material is the CONFIDENTIAL and PROPRIETARY
 * property of ActiveViam Limited. Any unauthorized use,
 * reproduction or transfer of this material is strictly prohibited
 */

package com.activeviam.apps.cfg.pivot;


import static com.activeviam.apps.cfg.pivot.PivotManagerConfig.INT_FORMATTER;
import static com.activeviam.apps.cfg.pivot.PivotManagerConfig.NATIVE_MEASURES;
import static com.activeviam.apps.cfg.pivot.PivotManagerConfig.TIMESTAMP_FORMATTER;

import java.util.concurrent.TimeUnit;

import javax.validation.constraints.NotNull;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import com.activeviam.apps.constants.StoreAndFieldConstants;
import com.activeviam.builders.StartBuilding;
import com.qfs.messenger.impl.NettyMessenger;
import com.quartetfs.biz.pivot.context.impl.QueriesTimeLimit;
import com.quartetfs.biz.pivot.definitions.IActivePivotInstanceDescription;
import com.quartetfs.biz.pivot.definitions.IMessengerDefinition;
import com.quartetfs.biz.pivot.distribution.IDistributedActivePivotInstanceDescription;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Configuration
public class DistributedCubeConfig {

	public static final String CUBE_NAME = "Cube";

	private final DimensionConfig dimensionConfig;
	private final MeasureConfig measureConfig;
	public final CubeSpec spec;

	public IActivePivotInstanceDescription createCubeDescription() {

		return StartBuilding.cube(CUBE_NAME)
				.withContributorsCount()
				.withinFolder(NATIVE_MEASURES)
				.withAlias("Count")
				.withFormatter(INT_FORMATTER)

				// WARN: This will not be available for AggregateProvider `jit`
				.withUpdateTimestamp()
				.withinFolder(NATIVE_MEASURES)
				.withAlias("Update.Timestamp")
				.withFormatter(TIMESTAMP_FORMATTER)
				.withCalculations(measureConfig::build)
				.withDimensions(dimensionConfig::build)

				.asDataCube()
				.withClusterDefinition()
				.withClusterId("Cluster")
				.withMessengerDefinition()
				.withKey(NettyMessenger.PLUGIN_KEY)
				.withProperty(IMessengerDefinition.AUTO_START, Boolean.TRUE.toString())
				.end()
				.end()
				.withApplicationId("app")
				.withAllHierarchies()
				.withAllMeasures()
				.end()

				// Aggregate provider
				.withAggregateProvider()
				.jit()

				// Shared context values
				// Query maximum execution time (before timeout cancellation): 30s
//				.withSharedContextValue(QueriesTimeLimit.of(30, TimeUnit.SECONDS))
//				.withSharedMdxContext()
//				.aggressiveFormulaEvaluation(true)
//				.end()
				.withSharedDrillthroughProperties()
				.withMaxRows(10000)
				.end()
				.build();
	}

	public IDistributedActivePivotInstanceDescription createQueryCubeDescription() {

		return StartBuilding.cube(CUBE_NAME)
				.asQueryCube()
				.withClusterDefinition()
				.withClusterId("Cluster")
				.withMessengerDefinition()
				.withKey(NettyMessenger.PLUGIN_KEY)
				.withProperty(IMessengerDefinition.AUTO_START, Boolean.TRUE.toString())
				.end()
				.end()
				.withApplication("app")
				.withDistributingFields(StoreAndFieldConstants.ASOFDATE)
				.end()

				// Shared context values
				// Query maximum execution time (before timeout cancellation): 30s
//				.withSharedContextValue(QueriesTimeLimit.of(30, TimeUnit.SECONDS))
//				.withSharedMdxContext()
//				.aggressiveFormulaEvaluation(true)
//				.end()
				.withSharedDrillthroughProperties()
				.withMaxRows(10000)
				.end()
				.build();
	}

	@Data
	@Validated
	@ConfigurationProperties(prefix = "cube")
	public static class CubeSpec {

		@NotNull
		private String name;
		private boolean datacube;
		private int year;
		private int month;
		private int day;

	}

}
