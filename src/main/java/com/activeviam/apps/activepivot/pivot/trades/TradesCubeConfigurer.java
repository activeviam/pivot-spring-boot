package com.activeviam.apps.activepivot.pivot.trades;

import com.activeviam.apps.activepivot.configurers.*;
import com.activeviam.apps.activepivot.configurers.annotation.InCube;
import com.activeviam.builders.StartBuilding;
import com.quartetfs.biz.pivot.context.impl.QueriesTimeLimit;
import com.quartetfs.biz.pivot.definitions.IActivePivotInstanceDescription;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.activeviam.apps.activepivot.pivot.CubeConstants.TRADES_CUBE_NAME;
import static com.activeviam.apps.activepivot.pivot.CubeConstants.TRADES_SCHEMA_NAME;

@Component("tradesCube")
// We use this qualifier to bind our cube to the correct schema
@OnSchema(TRADES_SCHEMA_NAME)
public class TradesCubeConfigurer implements ICubeConfigurer {

	private final List<IMeasuresConfigurer> measuresConfigurers;

	private final List<IDimensionsConfigurer> dimensionsConfigurers;

	public TradesCubeConfigurer(
			@InCube(TRADES_CUBE_NAME) @NotEmpty List<IMeasuresConfigurer> measuresConfigurers,
			@InCube(TRADES_CUBE_NAME) @NotEmpty List<IDimensionsConfigurer> dimensionsConfigurers
	) {
		this.measuresConfigurers = measuresConfigurers;
		this.dimensionsConfigurers = dimensionsConfigurers;
	}


	@Override
	public String cubeName() {
		return TRADES_CUBE_NAME;
	}

	@Override
	public IActivePivotInstanceDescription cubeDescription() {
		return StartBuilding.cube(cubeName())

				.withCalculations(context -> measuresConfigurers.forEach(configurer -> configurer.add(context)))
				.withDimensions(dimensionsConfigurers.get(0)::add)

				// Aggregate provider
				.withAggregateProvider()
				.jit()

				// Shared context values
				// Query maximum execution time (before timeout cancellation): 30s
				.withSharedContextValue(QueriesTimeLimit.of(30, TimeUnit.SECONDS))
				.withSharedMdxContext().aggressiveFormulaEvaluation(true).end()

				.withSharedDrillthroughProperties()
				.withMaxRows(10000)
				.end()
				.build();
	}
}
