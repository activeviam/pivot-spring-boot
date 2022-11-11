package com.activeviam.apps.pivot.configurers;

import org.springframework.stereotype.Component;

@Component
public interface ICubeConfigurer<T> {

	String cubeName();

	/**
	 * Configures the given builder in order to created the cube description.
	 *
	 *
	 * @return The configured builder
	 */
	T cubeDescription();

}
