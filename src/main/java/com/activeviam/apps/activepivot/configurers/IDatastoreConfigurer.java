/*
 * (C) ActiveViam 2022
 * ALL RIGHTS RESERVED. This material is the CONFIDENTIAL and PROPRIETARY
 * property of ActiveViam. Any unauthorized use,
 * reproduction or transfer of this material is strictly prohibited
 */
package com.activeviam.apps.activepivot.configurers;

import com.qfs.desc.IDatastoreSchemaDescription;

/**
 * @author ActiveViam
 */
public interface IDatastoreConfigurer {
	IDatastoreSchemaDescription datastoreSchemaDescription();

}
