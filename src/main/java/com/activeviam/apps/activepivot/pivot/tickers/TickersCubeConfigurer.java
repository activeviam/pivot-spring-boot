package com.activeviam.apps.activepivot.pivot.tickers;

import com.activeviam.apps.activepivot.configurers.*;
import com.activeviam.builders.StartBuilding;
import com.quartetfs.biz.pivot.context.impl.QueriesTimeLimit;
import com.quartetfs.biz.pivot.definitions.IActivePivotInstanceDescription;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

import static com.activeviam.apps.activepivot.pivot.CubeConstants.TICKERS_CUBE_NAME;
import static com.activeviam.apps.activepivot.pivot.CubeConstants.TICKERS_SCHEMA_NAME;

@Component
// We use this qualifier to bind our cube to the correct schema
@OnSchema(TICKERS_SCHEMA_NAME)
public class TickersCubeConfigurer implements ICubeConfigurer {

	private final IMeasuresConfigurer measuresConfigurer;

	private final IDimensionsConfigurer dimensionsConfigurer;

	public TickersCubeConfigurer(@OnCube(TICKERS_CUBE_NAME) TickersMeasuresConfigurer measuresConfigurer, @OnCube(TICKERS_CUBE_NAME) TickersDimensionsConfigurer dimensionsConfigurer) {
		this.measuresConfigurer = measuresConfigurer;
		this.dimensionsConfigurer = dimensionsConfigurer;
	}


	@Override
	public String cubeName() {
		return TICKERS_CUBE_NAME;
	}

	@Override
	public IActivePivotInstanceDescription cubeDescription() {
		return StartBuilding.cube(cubeName())

				.withCalculations(measuresConfigurer::add)
				.withDimensions(dimensionsConfigurer::add)

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
