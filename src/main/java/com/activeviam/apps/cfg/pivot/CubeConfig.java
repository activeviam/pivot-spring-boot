package com.activeviam.apps.cfg.pivot;

import com.activeviam.apps.constants.AppConstants;
import com.activeviam.desc.build.ICanBuildCubeDescription;
import com.activeviam.desc.build.ICubeDescriptionBuilder;
import com.activeviam.desc.build.dimensions.ICanStartBuildingDimensions;
import com.quartetfs.biz.pivot.context.impl.QueriesTimeLimit;
import com.quartetfs.biz.pivot.definitions.IActivePivotInstanceDescription;

import java.util.concurrent.TimeUnit;

import static com.activeviam.apps.cfg.pivot.PivotManagerConfig.INT_FORMATTER;
import static com.activeviam.apps.cfg.pivot.PivotManagerConfig.NATIVE_MEASURES;

public class CubeConfig {

    /**
     * Configures the given builder in order to created the cube description.
     *
     * @param builder The builder to configure
     * @return The configured builder
     */
    public static ICanBuildCubeDescription<IActivePivotInstanceDescription> configureCubeBuilder(
            final ICubeDescriptionBuilder.INamedCubeDescriptionBuilder builder) {

        return builder
                .withContributorsCount()
                .withinFolder(NATIVE_MEASURES)
                .withAlias("Count")
                .withFormatter(INT_FORMATTER)

                .withCalculations(Measures::build)
                .withDimensions(CubeConfig::dimensions)

                // Aggregate provider
                .withAggregateProvider()
                .jit()

                // Shared context values
                // Query maximum execution time (before timeout cancellation): 30s
                .withSharedContextValue(QueriesTimeLimit.of(30, TimeUnit.SECONDS))
                .withSharedMdxContext().aggressiveFormulaEvaluation(true).end()

                .withSharedDrillthroughProperties()
                .withMaxRows(10000)
                .end();
    }

    /**
     * Adds the dimensions descriptions to the input
     * builder.
     *
     * @param builder The cube builder
     * @return The builder for chained calls
     */
    public static ICanBuildCubeDescription<IActivePivotInstanceDescription> dimensions(ICanStartBuildingDimensions builder) {

        return builder
            .withSingleLevelDimension(AppConstants.SESSION_TIMESTAMP)
            .withSingleLevelDimension(AppConstants.BENCHMARK_INFO_PLUGIN_KEY)
            .withSingleLevelDimension(AppConstants.BENCHMARK_DATA_ID);


    }

}
