package com.activeviam.apps.activepivot.pivot;

import java.util.concurrent.TimeUnit;

import com.activeviam.apps.activepivot.configurers.ICubeConfigurer;
import com.activeviam.apps.activepivot.configurers.IDimensionsConfigurer;
import com.activeviam.apps.activepivot.configurers.IMeasuresConfigurer;
import com.activeviam.builders.StartBuilding;
import com.quartetfs.biz.pivot.context.impl.QueriesTimeLimit;
import com.quartetfs.biz.pivot.definitions.IActivePivotInstanceDescription;
import org.springframework.stereotype.Component;

import static com.activeviam.apps.activepivot.pivot.CubeConstants.*;

@Component
public class CubeConfigurer implements ICubeConfigurer {

	private final IMeasuresConfigurer measuresConfigurer;

	private final IDimensionsConfigurer dimensionsConfigurer;

	public CubeConfigurer(MeasuresConfigurer measuresConfigurer, DimensionsConfigurer dimensionsConfigurer) {
		this.measuresConfigurer = measuresConfigurer;
		this.dimensionsConfigurer = dimensionsConfigurer;
	}


	@Override
	public String cubeName() {
		return CUBE_NAME;
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
