/*
 * (C) ActiveViam 2022
 * ALL RIGHTS RESERVED. This material is the CONFIDENTIAL and PROPRIETARY
 * property of ActiveViam. Any unauthorized use,
 * reproduction or transfer of this material is strictly prohibited
 */
package com.activeviam.apps.pivot.configurers;

import com.qfs.desc.IDatastoreSchemaDescription;
import com.quartetfs.biz.pivot.definitions.IActivePivotManagerDescription;

/**
 * @author ActiveViam
 */
public interface IActivePivotManagerDescriptionConfigurer {

	IActivePivotManagerDescription managerDescription(IDatastoreSchemaDescription datastoreSchema);

}
