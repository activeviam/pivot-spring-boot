/*
 * Copyright (C) ActiveViam 2023
 * ALL RIGHTS RESERVED. This material is the CONFIDENTIAL and PROPRIETARY
 * property of ActiveViam Limited. Any unauthorized use,
 * reproduction or transfer of this material is strictly prohibited
 */
package com.activeviam.apps.cfg;

import org.springframework.context.annotation.Configuration;

import com.activeviam.apps.constants.StoreAndFieldConstants;
import com.activeviam.builders.StartBuilding;
import com.activeviam.desc.build.ISelectionDescriptionBuilder;
import com.quartetfs.biz.pivot.definitions.ISelectionDescription;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class DatastoreSelectionConfig {

    private final DatastoreSchemaConfig datastoreSchemaConfig;

    /**
     * Creates the {@link ISelectionDescription} for Pivot Schema.
     *
     * @return The created selection description
     */
    public ISelectionDescription createSchemaSelectionDescription() {
        return StartBuilding.selection(datastoreSchemaConfig.datastoreSchemaDescription())
                .fromBaseStore(StoreAndFieldConstants.TRADES_STORE_NAME)
                .withAllReachableFields(ISelectionDescriptionBuilder.FieldsCollisionHandler.CLOSEST)
                .build();
    }
}
