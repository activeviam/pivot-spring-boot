package com.activeviam.apps.activepivot.pivot.trades;

import com.activeviam.apps.activepivot.configurers.*;
import com.activeviam.builders.StartBuilding;
import com.quartetfs.biz.pivot.context.impl.QueriesTimeLimit;
import com.quartetfs.biz.pivot.definitions.IActivePivotInstanceDescription;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.activeviam.apps.activepivot.pivot.CubeConstants.TRADES_CUBE_NAME;
import static com.activeviam.apps.activepivot.pivot.CubeConstants.TRADES_SCHEMA_NAME;

@Component
// We use this qualifier to bind our cube to the correct schema
@OnSchema(TRADES_SCHEMA_NAME)
public class TradesCubeConfigurer implements ICubeConfigurer {

	private final IMeasuresConfigurer measuresConfigurer;

	private final List<IDimensionsConfigurer> dimensionsConfigurer;

	public TradesCubeConfigurer(
			@InCube(TRADES_CUBE_NAME) IMeasuresConfigurer measuresConfigurer,
			@InCube(TRADES_CUBE_NAME) List<IDimensionsConfigurer> dimensionsConfigurer
	) {
		this.measuresConfigurer = measuresConfigurer;
		this.dimensionsConfigurer = dimensionsConfigurer;
	}


	@Override
	public String cubeName() {
		return TRADES_CUBE_NAME;
	}

	@Override
	public IActivePivotInstanceDescription cubeDescription() {
		return StartBuilding.cube(cubeName())

				.withCalculations(measuresConfigurer::add)
				.withDimensions(dimensionsConfigurer.get(0)::add)

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
