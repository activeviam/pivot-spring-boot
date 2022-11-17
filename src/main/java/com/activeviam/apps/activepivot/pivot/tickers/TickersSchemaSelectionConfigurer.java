/*
 * (C) ActiveViam 2022
 * ALL RIGHTS RESERVED. This material is the CONFIDENTIAL and PROPRIETARY
 * property of ActiveViam. Any unauthorized use,
 * reproduction or transfer of this material is strictly prohibited
 */
package com.activeviam.apps.activepivot.pivot.tickers;

import com.activeviam.apps.activepivot.configurers.ICubeConfigurer;
import com.activeviam.apps.activepivot.configurers.ISchemaSelectionConfigurer;
import com.activeviam.apps.activepivot.configurers.OnSchema;
import com.activeviam.apps.activepivot.data.datastore.StoreAndFieldConstants;
import com.activeviam.builders.StartBuilding;
import com.qfs.desc.IDatastoreSchemaDescription;
import com.quartetfs.biz.pivot.definitions.ISelectionDescription;
import org.springframework.stereotype.Component;

import java.util.Collection;

import static com.activeviam.apps.activepivot.pivot.CubeConstants.TICKERS_SCHEMA_NAME;

/**
 * @author ActiveViam
 */
@Component
public class TickersSchemaSelectionConfigurer implements ISchemaSelectionConfigurer {

    private final Collection<ICubeConfigurer> cubeConfigurers;

    public TickersSchemaSelectionConfigurer(@OnSchema(TICKERS_SCHEMA_NAME) Collection<ICubeConfigurer> cubeConfigurers) {
        this.cubeConfigurers = cubeConfigurers;
    }

    @Override
    public String schemaName() {
        return TICKERS_SCHEMA_NAME;
    }

    /**
     * Creates the {@link ISelectionDescription} for Pivot Schema.
     *
     * @param datastoreSchemaDescription : The datastore description
     * @return The created selection description
     */
    @Override
    public ISelectionDescription createSchemaSelectionDescription(
            IDatastoreSchemaDescription datastoreSchemaDescription) {
        return StartBuilding.selection(datastoreSchemaDescription)
                .fromBaseStore(StoreAndFieldConstants.TRADES_DETAILS_STORE_NAME)
                .withAllReachableFields()
                .build();
    }

    @Override
    public Collection<ICubeConfigurer> cubes() {
        return cubeConfigurers;
    }
}