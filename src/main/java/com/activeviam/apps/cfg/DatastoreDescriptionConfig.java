package com.activeviam.apps.cfg;

import com.activeviam.apps.constants.StoreAndFieldConstants;
import com.qfs.desc.IDatastoreSchemaDescription;
import com.qfs.desc.IReferenceDescription;
import com.qfs.desc.IStoreDescription;
import com.qfs.desc.impl.DatastoreSchemaDescription;
import com.qfs.desc.impl.StoreDescriptionBuilder;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

import static com.qfs.literal.ILiteralType.*;

public class DatastoreDescriptionConfig {

    public static IStoreDescription createTradesStoreDescription() {
        return new StoreDescriptionBuilder()
                .withStoreName(StoreAndFieldConstants.TRADES_STORE_NAME)
                .withField(StoreAndFieldConstants.ASOFDATE, LOCAL_DATE)
                .asKeyField()
                .withField(StoreAndFieldConstants.TRADES_TRADEID, STRING)
                .asKeyField()
                .withField(StoreAndFieldConstants.TRADES_NOTIONAL, DOUBLE)
                .build();
    }

    public static IStoreDescription createScenarioStoreDescription() {
        return new StoreDescriptionBuilder()
                .withStoreName(StoreAndFieldConstants.SCENARIO_STORE_NAME)
                .withField(StoreAndFieldConstants.SCENARIO, STRING)
                .asKeyField()
                .withField(StoreAndFieldConstants.TREATMENT, STRING)
                .asKeyField()
                .build();
    }

    public static Collection<IReferenceDescription> references() {
        return Collections.emptyList();
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
    public static IDatastoreSchemaDescription schemaDescription() {

        final Collection<IStoreDescription> stores = new LinkedList<>();
        stores.add(createTradesStoreDescription());
        stores.add(createScenarioStoreDescription());

        return new DatastoreSchemaDescription(stores, references());
    }
}
