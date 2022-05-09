package com.activeviam.apps.cfg;

import com.activeviam.apps.constants.StoreAndFieldConstants;
import com.qfs.desc.IDatastoreSchemaDescription;
import com.qfs.desc.IReferenceDescription;
import com.qfs.desc.IStoreDescription;
import com.qfs.desc.impl.DatastoreSchemaDescription;
import com.qfs.desc.impl.ReferenceDescription;
import com.qfs.desc.impl.StoreDescriptionBuilder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

import static com.activeviam.apps.constants.StoreAndFieldConstants.*;
import static com.qfs.literal.ILiteralType.*;
import static com.quartetfs.biz.pivot.cube.hierarchy.axis.IAxisMember.DATA_MEMBER_DISCRIMINATOR;

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

    public static IStoreDescription createCategoriesStoreDescription() {
        return new StoreDescriptionBuilder()
                .withStoreName(GROUPS_STORE_NAME)
                .withField(StoreAndFieldConstants.ASOFDATE, LOCAL_DATE)
                .asKeyField()
                .withField(StoreAndFieldConstants.GROUP_ID, STRING)
                .asKeyField()
                .withField(StoreAndFieldConstants.TRADES_TRADEID, STRING)
                .asKeyField()
                .build();
    }

    public static IStoreDescription createWeightsStoreDescription() {
        return new StoreDescriptionBuilder()
                .withStoreName(WEIGHTS_STORE_NAME)
                .withField(StoreAndFieldConstants.ASOFDATE, LOCAL_DATE)
                .asKeyField()
                .withField(StoreAndFieldConstants.GROUP_ID, STRING)
                .asKeyField()
                .withField(GROUP_TYPE, STRING, "undefined")
                .withField(GROUP_LVL_1, STRING, DATA_MEMBER_DISCRIMINATOR)
                .withField(GROUP_LVL_2, STRING, DATA_MEMBER_DISCRIMINATOR)
                .withField(WEIGHT, DOUBLE)
                .build();
    }

    public static Collection<IReferenceDescription> references() {
        var references = new ArrayList<IReferenceDescription>();
        references.add(ReferenceDescription.builder()
                .fromStore(GROUPS_STORE_NAME)
                .toStore(WEIGHTS_STORE_NAME)
                .withName(WEIGHTS_STORE_NAME)
                .withMapping(ASOFDATE, ASOFDATE)
                .withMapping(GROUP_ID, GROUP_ID)
                .build());
        return references;
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
        stores.add(createCategoriesStoreDescription());
        stores.add(createWeightsStoreDescription());

        return new DatastoreSchemaDescription(stores, references());
    }
}
