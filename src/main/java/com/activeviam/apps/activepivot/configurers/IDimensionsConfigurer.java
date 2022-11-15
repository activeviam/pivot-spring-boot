/*
 * (C) ActiveViam 2022
 * ALL RIGHTS RESERVED. This material is the CONFIDENTIAL and PROPRIETARY
 * property of ActiveViam. Any unauthorized use,
 * reproduction or transfer of this material is strictly prohibited
 */
package com.activeviam.apps.activepivot.configurers;

import com.activeviam.desc.build.ICanBuildCubeDescription;
import com.activeviam.desc.build.dimensions.ICanStartBuildingDimensions;
import com.quartetfs.biz.pivot.definitions.IActivePivotInstanceDescription;
import org.springframework.stereotype.Component;

/**
 * @author ActiveViam
 */
@Component
public interface IDimensionsConfigurer {

	/**
	 * Adds the dimensions descriptions to the input builder.
	 *
	 * @param builder
	 *            The cube builder
	 * @return The builder for chained calls
	 */
	ICanBuildCubeDescription<IActivePivotInstanceDescription> publish(ICanStartBuildingDimensions builder);
}
