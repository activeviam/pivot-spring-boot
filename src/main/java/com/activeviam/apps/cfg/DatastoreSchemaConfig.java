/*
 * Copyright (C) ActiveViam 2023
 * ALL RIGHTS RESERVED. This material is the CONFIDENTIAL and PROPRIETARY
 * property of ActiveViam Limited. Any unauthorized use,
 * reproduction or transfer of this material is strictly prohibited
 */
package com.activeviam.apps.cfg;

import static com.activeviam.apps.constants.StoreAndFieldConstants.ASOFDATE;
import static com.activeviam.apps.constants.StoreAndFieldConstants.INSTRUMENT;
import static com.activeviam.apps.constants.StoreAndFieldConstants.INSTRUMENTS_STORE_NAME;
import static com.activeviam.apps.constants.StoreAndFieldConstants.TRADES_STORE_NAME;
import static com.qfs.literal.ILiteralType.DOUBLE;
import static com.qfs.literal.ILiteralType.LOCAL_DATE;
import static com.qfs.literal.ILiteralType.STRING;

import java.util.Set;

import org.springframework.context.annotation.Configuration;

import com.activeviam.apps.constants.StoreAndFieldConstants;
import com.activeviam.builders.StartBuilding;
import com.qfs.desc.IDatastoreSchemaDescription;
import com.qfs.desc.IReferenceDescription;
import com.qfs.desc.IStoreDescription;
import com.qfs.desc.impl.DatastoreSchemaDescription;
import com.qfs.server.cfg.IDatastoreSchemaDescriptionConfig;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class DatastoreSchemaConfig implements IDatastoreSchemaDescriptionConfig {

    private IStoreDescription tradesStoreDescription() {
        return StartBuilding.store()
                .withStoreName(TRADES_STORE_NAME)
                .withField(StoreAndFieldConstants.ASOFDATE, LOCAL_DATE)
                .asKeyField()
                .withField(StoreAndFieldConstants.TRADES_TRADEID, STRING)
                .asKeyField()
                .withField(StoreAndFieldConstants.INSTRUMENT, STRING)
                .withField(StoreAndFieldConstants.TRADES_NOTIONAL, DOUBLE)
                .build();
    }

    private IStoreDescription instrumentStoreDescription() {
        return StartBuilding.store()
                .withStoreName(StoreAndFieldConstants.INSTRUMENTS_STORE_NAME)
                .withField(StoreAndFieldConstants.ASOFDATE, LOCAL_DATE)
                .asKeyField()
                .withField(StoreAndFieldConstants.INSTRUMENT, STRING)
                .asKeyField()
                .withField(StoreAndFieldConstants.SCOPE_CONSTANT, STRING)
                .build();
    }

    private IReferenceDescription tradesToInstrumentReference() {
        return StartBuilding.reference()
                .fromStore(TRADES_STORE_NAME)
                .toStore(INSTRUMENTS_STORE_NAME)
                .withName("TradesToInstruments")
                .withMapping(ASOFDATE, ASOFDATE)
                .withMapping(INSTRUMENT, INSTRUMENT)
                .build();
    }

    @Override
    public IDatastoreSchemaDescription datastoreSchemaDescription() {
        var stores = Set.of(tradesStoreDescription(), instrumentStoreDescription());
        var references = Set.of(tradesToInstrumentReference());
        return new DatastoreSchemaDescription(stores, references);
    }
}
