package com.activeviam.apps.activepivot.pivot;

import java.util.concurrent.TimeUnit;
import com.activeviam.builders.StartBuilding;
import com.quartetfs.biz.pivot.context.impl.QueriesTimeLimit;
import com.quartetfs.biz.pivot.definitions.IActivePivotInstanceDescription;
import org.springframework.stereotype.Component;

import static com.activeviam.apps.activepivot.pivot.CubeConstants.*;

@Component
public class CubeConfigurer {

	private final MeasuresConfigurer measuresConfigurer;

	private final DimensionsConfigurer dimensionsConfigurer;

	public CubeConfigurer(MeasuresConfigurer measuresConfigurer, DimensionsConfigurer dimensionsConfigurer) {
		this.measuresConfigurer = measuresConfigurer;
		this.dimensionsConfigurer = dimensionsConfigurer;
	}


	public String cubeName() {
		return CUBE_NAME;
	}

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
