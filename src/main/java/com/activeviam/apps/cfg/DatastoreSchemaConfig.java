/*
 * Copyright (C) ActiveViam 2023
 * ALL RIGHTS RESERVED. This material is the CONFIDENTIAL and PROPRIETARY
 * property of ActiveViam Limited. Any unauthorized use,
 * reproduction or transfer of this material is strictly prohibited
 */
package com.activeviam.apps.cfg;

import static com.activeviam.apps.constants.StoreAndFieldConstants.TRADE_PNL_TRADE_ID;
import static com.qfs.literal.ILiteralType.DOUBLE;
import static com.qfs.literal.ILiteralType.INT;
import static com.qfs.literal.ILiteralType.LOCAL_DATE;
import static com.qfs.literal.ILiteralType.STRING;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

import org.springframework.context.annotation.Configuration;

import com.activeviam.apps.constants.StoreAndFieldConstants;
import com.qfs.desc.IDatastoreSchemaDescription;
import com.qfs.desc.IReferenceDescription;
import com.qfs.desc.IStoreDescription;
import com.qfs.desc.impl.DatastoreSchemaDescription;
import com.qfs.desc.impl.DuplicateKeyHandlers;
import com.qfs.desc.impl.StoreDescription;
import com.qfs.server.cfg.IDatastoreSchemaDescriptionConfig;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class DatastoreSchemaConfig implements IDatastoreSchemaDescriptionConfig {

    public static final int NUM_HASH_PARTITIONS = Runtime.getRuntime().availableProcessors();

    private IStoreDescription createTradePnLStoreDescription() {
        //    Dataset => Westpac/Market Risk Reports/active_pivot_poc/trade_pnl_1d_var
        //    AsOfDate         DATE
        //    TradeId          VARCHAR
        //    ScenarioSet      VARCHAR
        //    CalculationId    VARCHAR
        //    RiskFactor       VARCHAR
        //    RiskClass        VARCHAR
        //    SensitivityName  VARCHAR
        //    LiquidityHorizon INTEGER
        //    Ccy              VARCHAR
        //    MTM              DOUBLE
        //    PnL[]            DOUBLE[]
        return StoreDescription.builder()
                .withStoreName(StoreAndFieldConstants.TRADE_PNL_STORE_NAME)
                .withField(StoreAndFieldConstants.ASOFDATE, LOCAL_DATE)
                .asKeyField()
                .withField(TRADE_PNL_TRADE_ID, STRING)
                .asKeyField()
                .withField(StoreAndFieldConstants.TRADE_PNL_SCENARIO_SET, STRING)
                .asKeyField()
                .withField(StoreAndFieldConstants.TRADE_PNL_CALCLATION_ID, STRING)
                .asKeyField()
                .withField(StoreAndFieldConstants.TRADE_PNL_RISK_FACTOR, STRING)
                .asKeyField()
                .withField(StoreAndFieldConstants.TRADE_PNL_RISK_CLASS, STRING)
                .dictionarized()
                .withField(StoreAndFieldConstants.TRADE_PNL_SENSITIVITY_NAME, STRING)
                .dictionarized()
                .withField(StoreAndFieldConstants.TRADE_PNL_LIQUIDITY_HORIZON, INT)
                .asKeyField()
                .withField(StoreAndFieldConstants.TRADE_PNL_CCY, STRING)
                .dictionarized()
                .withField(StoreAndFieldConstants.TRADE_PNL_MTM, DOUBLE)
//                .withVectorField(StoreAndFieldConstants.TRADE_PNL_PNL_VECTOR, DOUBLE)
                .withModuloPartitioning(TRADE_PNL_TRADE_ID, NUM_HASH_PARTITIONS)
                .withDuplicateKeyHandler(DuplicateKeyHandlers.LOG_WITHIN_TRANSACTION)
                .build();
    }

    private Collection<IReferenceDescription> references() {
        return Collections.emptyList();
    }

    @Override
    public IDatastoreSchemaDescription datastoreSchemaDescription() {
        final Collection<IStoreDescription> stores = new LinkedList<>();
        stores.add(createTradePnLStoreDescription());

        return new DatastoreSchemaDescription(stores, references());
    }
}
