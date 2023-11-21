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

import org.springframework.context.annotation.Configuration;

import com.activeviam.apps.cfg.IDistributedInfo;
import com.activeviam.apps.constants.StoreAndFieldConstants;
import com.activeviam.builders.StartBuilding;
import com.activeviam.desc.build.ICanBuildCubeDescription;
import com.activeviam.desc.build.ICubeDescriptionBuilder;
import com.qfs.messenger.impl.NettyMessenger;
import com.quartetfs.biz.pivot.context.impl.QueriesTimeLimit;
import com.quartetfs.biz.pivot.definitions.IActivePivotInstanceDescription;
import com.quartetfs.biz.pivot.definitions.IMessengerDefinition;
import com.quartetfs.biz.pivot.distribution.IDistributedActivePivotInstanceDescription;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Configuration
public class CubeConfig {
    public static final String CUBE_NAME = "Cube";

    private final DimensionConfig dimensionConfig;
    private final MeasureConfig measureConfig;
    private final IDistributedInfo distributedInfo;

    public static final String CLUSTER_ID = "my_cluster_just_for_fun";
    public static final String APPLICATION_ID = "application_just_for_fun";

    /**
     * Configures the given builder in order to created the cube description.
     *
     * @param builder
     *            The builder to configure
     * @return The configured builder
     */
    private ICanBuildCubeDescription<IActivePivotInstanceDescription> configureCubeBuilder(
            final ICubeDescriptionBuilder.INamedCubeDescriptionBuilder builder) {

        return builder.withContributorsCount()
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

                // Aggregate provider
                .withAggregateProvider()
                .jit()

                // Shared context values
                // Query maximum execution time (before timeout cancellation): 30s
                .withSharedContextValue(QueriesTimeLimit.of(30, TimeUnit.SECONDS))
                .withSharedMdxContext()
                .aggressiveFormulaEvaluation(true)
                .end()
                .withSharedDrillthroughProperties()
                .withMaxRows(10000)

                .end();
    }

    public IActivePivotInstanceDescription createCubeDescription() {
        return configureCubeBuilder(StartBuilding.cube(this.distributedInfo.getCubeName()))
                .asDataCube()
                .withClusterDefinition()
                .withClusterId(CLUSTER_ID)
                .withMessengerDefinition()
                .withKey(NettyMessenger.PLUGIN_KEY)
                .withProperty(IMessengerDefinition.AUTO_START, Boolean.TRUE.toString())
                .end()
                .withProtocolPath(this.distributedInfo.getProtocolPath())
                .end()
                .withApplicationId(APPLICATION_ID)
                .withAllHierarchies()
                .withAllMeasures()
                .end()
                .build();
    }

    public IDistributedActivePivotInstanceDescription createQueryCubeDescription() {
        return StartBuilding.cube(this.distributedInfo.getCubeName())
                .asQueryCube()
                .withClusterDefinition()
                .withClusterId(CLUSTER_ID)
                .withMessengerDefinition()
                .withKey(NettyMessenger.PLUGIN_KEY)
                .withProperty(IMessengerDefinition.AUTO_START, Boolean.TRUE.toString())
                .end()
                .withProtocolPath(this.distributedInfo.getProtocolPath())
                .end()
                .withApplication(APPLICATION_ID)
                .withDistributingFields(StoreAndFieldConstants.ASOFDATE)
                .end()
                .build();
    }
}
