package com.activeviam.training.activepivot.store;

import com.activeviam.builders.StartBuilding;
import com.qfs.desc.IReferenceDescription;
import com.qfs.desc.IStoreDescription;
import com.qfs.desc.impl.StoreDescriptionBuilder;
import com.activeviam.training.constants.StoreAndFieldConstants;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.qfs.literal.ILiteralType.*;

@Configuration
public class DatastoreDescriptionConfig {

    @Bean
    public IStoreDescription createTradesStoreDescription() {
        return new StoreDescriptionBuilder().withStoreName(StoreAndFieldConstants.TRADES_STORE_NAME)
                .withField(StoreAndFieldConstants.ASOFDATE, LOCAL_DATE).asKeyField()
                .withField(StoreAndFieldConstants.TRADES_TRADEID, STRING).asKeyField()
                .withField(StoreAndFieldConstants.TRADES_NOTIONAL, DOUBLE)
                .withField(StoreAndFieldConstants.INSTRUMENT_ID, STRING)
                .build();
    }

    @Bean
    public IStoreDescription createInstrumentStoreDescription() {
        return new StoreDescriptionBuilder().withStoreName(StoreAndFieldConstants.INSTRUMENT_STORE_NAME)
                .withField(StoreAndFieldConstants.INSTRUMENT_ID, STRING).asKeyField()
                .withField(StoreAndFieldConstants.INSTRUMENT_NAME, STRING)
                .withField(StoreAndFieldConstants.INSTRUMENT_RATING, STRING)
                .build();
    }


    @Bean
    public IReferenceDescription ref1() {
        return StartBuilding.reference().fromStore(StoreAndFieldConstants.TRADES_STORE_NAME)
                .toStore(StoreAndFieldConstants.INSTRUMENT_STORE_NAME)
                .withName(StoreAndFieldConstants.TRADES_TO_INSTRUMENT)
                .withMapping(StoreAndFieldConstants.INSTRUMENT_ID, StoreAndFieldConstants.INSTRUMENT_ID)
                .build();
    }

//    @Bean
//    public IReferenceDescription ref2() {
//        return StartBuilding.reference().fromStore(StoreAndFieldConstants.TRADES_STORE_NAME)
//                .toStore(StoreAndFieldConstants.INSTRUMENT_STORE_NAME)
//                .withName(StoreAndFieldConstants.TRADES_TO_INSTRUMENT)
//                .withMapping(StoreAndFieldConstants.TRADES_TRADEID, StoreAndFieldConstants.TRADES_TRADEID)
//                .build();
//
//    }


}
