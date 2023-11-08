/*
 * Copyright (C) ActiveViam 2023
 * ALL RIGHTS RESERVED. This material is the CONFIDENTIAL and PROPRIETARY
 * property of ActiveViam Limited. Any unauthorized use,
 * reproduction or transfer of this material is strictly prohibited
 */
package com.activeviam.apps.cfg;

import static com.qfs.literal.ILiteralType.DOUBLE;
import static com.qfs.literal.ILiteralType.LOCAL_DATE;
import static com.qfs.literal.ILiteralType.STRING;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.springframework.context.annotation.Configuration;

import com.activeviam.apps.constants.StoreAndFieldConstants;
import com.qfs.desc.IDatastoreSchemaDescription;
import com.qfs.desc.IReferenceDescription;
import com.qfs.desc.IStoreDescription;
import com.qfs.desc.impl.DatastoreSchemaDescription;
import com.qfs.desc.impl.ReferenceDescription;
import com.qfs.desc.impl.StoreDescription;
import com.qfs.server.cfg.IDatastoreSchemaDescriptionConfig;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class DatastoreSchemaConfig implements IDatastoreSchemaDescriptionConfig {

    private IStoreDescription createTradesStoreDescription() {
        return StoreDescription.builder()
                .withStoreName(StoreAndFieldConstants.TRADES_STORE_NAME)
                .withField(StoreAndFieldConstants.ASOFDATE, LOCAL_DATE)
                .asKeyField()
                .withField(StoreAndFieldConstants.TRADES_TRADEID, STRING)
                .asKeyField()
                .withField(StoreAndFieldConstants.TRADES_DESK)
                .withField(StoreAndFieldConstants.TRADES_NOTIONAL, DOUBLE)
                .build();
    }

    private IStoreDescription createDesksStoreDescription() {
        return StoreDescription.builder()
                .withStoreName(StoreAndFieldConstants.DESKS_STORE_NAME)
                .withField(StoreAndFieldConstants.DESKS_DESK)
                .asKeyField()
                .withField(StoreAndFieldConstants.DESKS_COUNTRY, STRING)
                .build();
    }

    private IReferenceDescription createReferenceDescription() {
        return ReferenceDescription.builder()
                .fromStore(StoreAndFieldConstants.TRADES_STORE_NAME)
                .toStore(StoreAndFieldConstants.DESKS_STORE_NAME)
                .withName(StoreAndFieldConstants.REFERENCE_NAME)
                .withMapping(StoreAndFieldConstants.TRADES_DESK, StoreAndFieldConstants.DESKS_DESK)
                .build();
    }

    private Collection<IReferenceDescription> references() {
        return List.of(createReferenceDescription());
    }

    @Override
    public IDatastoreSchemaDescription datastoreSchemaDescription() {
        final Collection<IStoreDescription> stores = new LinkedList<>();
        stores.add(createTradesStoreDescription());
        stores.add(createDesksStoreDescription());

        return new DatastoreSchemaDescription(stores, references());
    }
}
