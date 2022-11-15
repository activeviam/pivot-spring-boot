/*
 * (C) ActiveViam 2022
 * ALL RIGHTS RESERVED. This material is the CONFIDENTIAL and PROPRIETARY
 * property of ActiveViam. Any unauthorized use,
 * reproduction or transfer of this material is strictly prohibited
 */
package com.activeviam.apps.activepivot.configurers;

import com.activeviam.desc.build.IActivePivotManagerDescriptionBuilder;
import com.qfs.desc.IDatastoreSchemaDescription;
import com.quartetfs.biz.pivot.definitions.ISelectionDescription;
import org.springframework.context.annotation.Configuration;

import java.util.Collection;

/**
 * @author ActiveViam
 */
@Configuration
public interface ISchemaSelectionConfigurer {

	/**
	 * Creates the {@link ISelectionDescription} for Pivot Schema.
	 *
	 * @return The created selection description
	 */

	String schemaName();

	ISelectionDescription createSchemaSelectionDescription(IDatastoreSchemaDescription schemaDescription);

	Collection<ICubeConfigurer> cubes();

}
