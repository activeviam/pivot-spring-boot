package com.activeviam.apps.cfg.pivot;

import static com.activeviam.apps.cfg.pivot.PivotManagerConfig.INT_FORMATTER;
import static com.activeviam.apps.cfg.pivot.PivotManagerConfig.NATIVE_MEASURES;
import static com.activeviam.apps.cfg.pivot.PivotManagerConfig.TIMESTAMP_FORMATTER;

import com.activeviam.builders.StartBuilding;
import com.activeviam.desc.build.ICanBuildCubeDescription;
import com.activeviam.desc.build.ICubeDescriptionBuilder;
import com.quartetfs.biz.pivot.context.impl.QueriesTimeLimit;
import com.quartetfs.biz.pivot.definitions.IActivePivotInstanceDescription;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;

@RequiredArgsConstructor
@Configuration
public class CubeConfig {
    public static final String CUBE_NAME = "Cube";

    private final DimensionConfig dimensionConfig;
    private final MeasureConfig measureConfig;

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
        return configureCubeBuilder(StartBuilding.cube(CUBE_NAME)).build();
    }
}
