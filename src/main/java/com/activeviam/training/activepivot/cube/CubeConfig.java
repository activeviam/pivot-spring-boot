package com.activeviam.training.activepivot.cube;

import com.activeviam.builders.StartBuilding;
import com.activeviam.copper.ICopperContext;
import com.activeviam.copper.api.Publishable;
import com.activeviam.desc.build.ICanBuildCubeDescription;
import com.activeviam.desc.build.ICubeDescriptionBuilder;
import com.activeviam.desc.build.dimensions.ICanStartBuildingDimensions;
import com.activeviam.training.activepivot.PivotManagerConfig;
import com.quartetfs.biz.pivot.context.impl.QueriesTimeLimit;
import com.quartetfs.biz.pivot.definitions.IActivePivotInstanceDescription;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Configuration
public class CubeConfig {


    private final ICanStartBuildingDimensions.DimensionsAdder dimensionsAdder;
    private final List<Publishable<?>> copperPublishables;

    public CubeConfig(
            ICanStartBuildingDimensions.DimensionsAdder dimensionsAdder,
            List<Publishable<?>> copperPublishables) {
        this.copperPublishables = copperPublishables;
        this.dimensionsAdder = dimensionsAdder;
    }


    @Autowired
    Environment env;


    @Bean
    public IActivePivotInstanceDescription createCubeDescription() {
        return configureCubeBuilder(StartBuilding.cube(PivotManagerConfig.CUBE_NAME)).build();
    }

    /**
     * Configures the given builder in order to created the cube description.
     *
     * @param builder The builder to configure
     * @return The configured builder
     */
    public ICanBuildCubeDescription<IActivePivotInstanceDescription> configureCubeBuilder(
            final ICubeDescriptionBuilder.INamedCubeDescriptionBuilder builder) {

        return builder
                .withContributorsCount()
                .withinFolder(PivotManagerConfig.NATIVE_MEASURES)
                .withAlias("Count")
                .withFormatter(PivotManagerConfig.INT_FORMATTER)

                .withCalculations(this::publishCopperElements)
                .withDimensions(dimensionsAdder)

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

    private void publishCopperElements(ICopperContext context) {
        copperPublishables.forEach(publishable -> publishable.publish(context));
    }
}
