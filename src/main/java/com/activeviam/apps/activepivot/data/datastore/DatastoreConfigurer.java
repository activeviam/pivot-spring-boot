package com.activeviam.apps.activepivot.data.datastore;

import static com.activeviam.apps.activepivot.data.datastore.StoreAndFieldConstants.*;
import static com.qfs.literal.ILiteralType.DOUBLE;
import static com.qfs.literal.ILiteralType.LOCAL_DATE;
import static com.qfs.literal.ILiteralType.STRING;

import com.activeviam.builders.StartBuilding;
import com.qfs.desc.IDatastoreSchemaDescription;
import com.qfs.desc.IReferenceDescription;
import com.qfs.desc.IStoreDescription;
import org.springframework.stereotype.Component;

@Component
public class DatastoreConfigurer {
    private static IStoreDescription createTradesStoreDescription() {
        return StartBuilding.store()
                .withStoreName(TRADES_STORE_NAME)
                .withField(ASOFDATE, LOCAL_DATE)
                .asKeyField()
                .withField(TRADES_TRADEID, STRING)
                .asKeyField()
                .withField(TRADES_NOTIONAL, DOUBLE)
                .build();
    }

    private static IStoreDescription createTradesDetailsStoreDescription() {
        return StartBuilding.store()
                .withStoreName(TRADES_DETAILS_STORE_NAME)
                .withField(TRADES_TRADEID, STRING)
                .asKeyField()
                .withField(TRADES_TICKER, STRING)
                .build();
    }

    private static IReferenceDescription tradeToDetailsReferences() {
        return StartBuilding.reference()
                .fromStore(TRADES_STORE_NAME)
                .toStore(TRADES_DETAILS_STORE_NAME)
                .withName(TRADES_DETAILS_STORE_NAME)
                .withMapping(TRADES_TRADEID, TRADES_TRADEID)
                .build();
    }

    /**
     *
     * Provide the schema description of the datastore.
     * <p>
     * It is based on the descriptions of the stores in the datastore, the descriptions of the references between those stores, and the optimizations and
     * constraints set on the schema.
     *
     * @return schema description
     */
    public IDatastoreSchemaDescription datastoreSchemaDescription() {
        return StartBuilding.datastoreSchema()
                .withStore(createTradesStoreDescription())
                .withStore(createTradesDetailsStoreDescription())
                .withReference(tradeToDetailsReferences())
                .build();
    }
}
