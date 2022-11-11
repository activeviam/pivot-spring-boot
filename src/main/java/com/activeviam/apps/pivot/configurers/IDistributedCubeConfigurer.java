/*
 * (C) ActiveViam 2022
 * ALL RIGHTS RESERVED. This material is the CONFIDENTIAL and PROPRIETARY
 * property of ActiveViam. Any unauthorized use,
 * reproduction or transfer of this material is strictly prohibited
 */
package com.activeviam.apps.pivot.configurers;

import com.quartetfs.biz.pivot.distribution.IDistributedActivePivotInstanceDescription;

/**
 * @author ActiveViam
 */
public interface IDistributedCubeConfigurer extends ICubeConfigurer<IDistributedActivePivotInstanceDescription> {

	String cubeName();

	/**
	 * Configures the given builder in order to created the cube description.
	 *
	 *
	 * @return The configured builder
	 */
	IDistributedActivePivotInstanceDescription cubeDescription();
}
