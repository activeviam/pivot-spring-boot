package com.activeviam.apps.pivot.configurers;

import com.quartetfs.biz.pivot.definitions.IActivePivotInstanceDescription;
import org.springframework.stereotype.Component;

@Component
public interface ILocalCubeConfigurer extends ICubeConfigurer<IActivePivotInstanceDescription> {

	String cubeName();

	/**
	 * Configures the given builder in order to created the cube description.
	 *
	 *
	 * @return The configured builder
	 */
	IActivePivotInstanceDescription cubeDescription();

}
