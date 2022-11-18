package com.activeviam.apps.activepivot.pivot.tickers;

import com.activeviam.apps.activepivot.configurers.*;
import com.activeviam.apps.activepivot.configurers.annotation_multivalue.InCube;
import com.activeviam.apps.activepivot.configurers.annotation_repeatable.Cube;
import com.activeviam.builders.StartBuilding;
import com.quartetfs.biz.pivot.context.impl.QueriesTimeLimit;
import com.quartetfs.biz.pivot.definitions.IActivePivotInstanceDescription;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.activeviam.apps.activepivot.pivot.CubeConstants.TICKERS_CUBE_NAME;
import static com.activeviam.apps.activepivot.pivot.CubeConstants.TICKERS_SCHEMA_NAME;

@Component("tickersCube")
// We use this qualifier to bind our cube to the correct schema
@OnSchema(TICKERS_SCHEMA_NAME)
public class TickersCubeConfigurer implements ICubeConfigurer {

	private final List<IMeasuresConfigurer> measuresConfigurers;

	private final List<IDimensionsConfigurer> dimensionsConfigurers;

	public TickersCubeConfigurer(
			@Cube(TICKERS_CUBE_NAME) @NotEmpty List<IMeasuresConfigurer> measuresConfigurer,
			@Cube(TICKERS_CUBE_NAME) @NotEmpty List<IDimensionsConfigurer> dimensionsConfigurer
	) {
		this.measuresConfigurers = measuresConfigurer;
		this.dimensionsConfigurers = dimensionsConfigurer;
	}


	@Override
	public String cubeName() {
		return TICKERS_CUBE_NAME;
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
