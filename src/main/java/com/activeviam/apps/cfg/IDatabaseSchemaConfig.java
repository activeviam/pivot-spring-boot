/*
 * Copyright (C) ActiveViam 2023
 * ALL RIGHTS RESERVED. This material is the CONFIDENTIAL and PROPRIETARY
 * property of ActiveViam Limited. Any unauthorized use,
 * reproduction or transfer of this material is strictly prohibited
 */

package com.activeviam.apps.cfg;

import org.springframework.context.annotation.Configuration;

import com.activeviam.database.api.schema.IDatabaseSchema;

@Configuration
public interface IDatabaseSchemaConfig {

    IDatabaseSchema getSchema();

}
