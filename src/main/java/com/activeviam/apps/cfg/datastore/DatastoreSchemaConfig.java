/*
 * Copyright (C) ActiveViam 2023
 * ALL RIGHTS RESERVED. This material is the CONFIDENTIAL and PROPRIETARY
 * property of ActiveViam Limited. Any unauthorized use,
 * reproduction or transfer of this material is strictly prohibited
 */
package com.activeviam.apps.cfg.datastore;

import static com.qfs.literal.ILiteralType.DOUBLE;
import static com.qfs.literal.ILiteralType.LOCAL_DATE;
import static com.qfs.literal.ILiteralType.STRING;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.activeviam.apps.cfg.IDatabaseSchemaConfig;
import com.activeviam.apps.constants.StoreAndFieldConstants;
import com.activeviam.database.api.schema.IDatabaseSchema;
import com.qfs.desc.IDatastoreSchemaDescription;
import com.qfs.desc.IReferenceDescription;
import com.qfs.desc.IStoreDescription;
import com.qfs.desc.impl.DatastoreSchemaDescription;
import com.qfs.desc.impl.StoreDescription;
import com.qfs.server.cfg.IDatastoreSchemaDescriptionConfig;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
@Profile({"datastore", "query"})
public class DatastoreSchemaConfig implements IDatastoreSchemaDescriptionConfig, IDatabaseSchemaConfig {

    private IStoreDescription createTradesStoreDescription() {
        return StoreDescription.builder()
                .withStoreName(StoreAndFieldConstants.TRADES_STORE_NAME)
                .withField(StoreAndFieldConstants.ASOFDATE, LOCAL_DATE)
                .asKeyField()
                .withField(StoreAndFieldConstants.TRADES_TRADEID, STRING)
                .asKeyField()
                .withField(StoreAndFieldConstants.TRADES_NOTIONAL, DOUBLE)
                .build();
    }

    private Collection<IReferenceDescription> references() {
        return Collections.emptyList();
    }

    @Override
    public IDatastoreSchemaDescription datastoreSchemaDescription() {
        final Collection<IStoreDescription> stores = new LinkedList<>();
        stores.add(createTradesStoreDescription());

        return new DatastoreSchemaDescription(stores, references());
    }

    @Override
    public IDatabaseSchema getSchema() {
        return datastoreSchemaDescription().asDatabaseSchema();
    }
}
