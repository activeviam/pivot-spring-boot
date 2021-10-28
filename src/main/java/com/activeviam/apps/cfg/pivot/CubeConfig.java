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
	 * Adds the dimensions descriptions to the input builder.
	 *
	 * @param builder The cube builder
	 * @return The builder for chained calls
	 */
	public static ICanBuildCubeDescription<IActivePivotInstanceDescription> dimensions(
			ICanStartBuildingDimensions builder) {

		return builder
				.withDimension(AppConstants.SESSION_DIMENSION)
				.withHierarchy(AppConstants.TIME_HIERARCHY)
				.withLevel(AppConstants.TIMESTAMP_LEVEL)

				.withHierarchy(AppConstants.CODE_VERSION_HIERARCHY)
				.withLevel(AppConstants.AP_VERSION_LEVEL)
				.withLevel(AppConstants.BRANCH_NAME_LEVEL)
				.withLevel(AppConstants.SHA1_LEVEL)
				.withLevel(AppConstants.SESSION_ID_LEVEL)

				.withDimension(AppConstants.BENCHMARK_DIMENSION)
				.withHierarchy(AppConstants.BENCHMARK_HIERARCHY)
				.slicing()
				.withLevel(AppConstants.BENCHMARK_KEY_LEVEL)
				.withLevel(AppConstants.BENCHMARK_TAGS_LEVEL)

				.withDimension(AppConstants.BENCHMARK_PARAMETERS_DIMENSION)
				.withSingleLevelHierarchies(
						AppConstants.BENCHMARK_P1_LEVEL,
						AppConstants.BENCHMARK_P2_LEVEL,
						AppConstants.BENCHMARK_P3_LEVEL,
						AppConstants.BENCHMARK_P4_LEVEL,
						AppConstants.BENCHMARK_P5_LEVEL,
						AppConstants.BENCHMARK_P6_LEVEL,
						AppConstants.BENCHMARK_P7_LEVEL,
						AppConstants.BENCHMARK_P8_LEVEL);
	}

}
