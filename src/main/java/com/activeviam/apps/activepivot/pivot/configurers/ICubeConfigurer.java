package com.activeviam.apps.activepivot.pivot.configurers;

import com.quartetfs.biz.pivot.definitions.IActivePivotInstanceDescription;
import org.springframework.stereotype.Component;

@Component
public interface ICubeConfigurer {

	String cubeName();

	/**
	 * Configures the given builder in order to created the cube description.
	 *
	 *
	 * @return The configured builder
	 */
	IActivePivotInstanceDescription cubeDescription();

}
