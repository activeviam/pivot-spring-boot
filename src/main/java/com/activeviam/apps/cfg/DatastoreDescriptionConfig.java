package com.activeviam.apps.cfg;

import com.activeviam.apps.constants.StoreAndFieldConstants;
import com.qfs.desc.IDatastoreSchemaDescription;
import com.qfs.desc.IReferenceDescription;
import com.qfs.desc.IStoreDescription;
import com.qfs.desc.impl.DatastoreSchemaDescription;
import com.qfs.desc.impl.ReferenceDescription;
import com.qfs.desc.impl.ReferenceDescriptionBuilder;
import com.qfs.desc.impl.StoreDescriptionBuilder;
import org.springframework.context.annotation.Configuration;

import java.util.Collection;
import java.util.LinkedList;

import static com.activeviam.apps.constants.StoreAndFieldConstants.*;
import static com.qfs.literal.ILiteralType.*;

@Configuration
public class DatastoreDescriptionConfig {

    public static IStoreDescription createPositionsStoreDescription() {
        return new StoreDescriptionBuilder().withStoreName(POSITIONS_STORE_NAME)
                .withField(StoreAndFieldConstants.POSITION_ID, INT).asKeyField()
                .withField(INSTRUMENT_ID, INT)
                .withField(StoreAndFieldConstants.L1, STRING)
                .withField(StoreAndFieldConstants.L2, STRING)
                .withField(StoreAndFieldConstants.L3, STRING)
                .withField(StoreAndFieldConstants.PNL, DOUBLE)
                .build();
    }

    public static IStoreDescription createInstrumentsStoreDescription() {
        return new StoreDescriptionBuilder().withStoreName(INSTRUMENTS_STORE_NAME)
                .withField(INSTRUMENT_ID, INT).asKeyField()
                .withField(StoreAndFieldConstants.DELTA, DOUBLE)
                .build();
    }

    public static Collection<IReferenceDescription> references() {
        final Collection<IReferenceDescription> references = new LinkedList<>();

        references.add(ReferenceDescription.builder()
                .fromStore(POSITIONS_STORE_NAME)
                .toStore(INSTRUMENTS_STORE_NAME)
                .withName("Pos2Inst")
                .withMapping(INSTRUMENT_ID, INSTRUMENT_ID)
                .build());

        return references;
    }



    /**
     *
     * Provide the schema description of the datastore.
     * <p>
     * It is based on the descriptions of the stores in the datastore, the descriptions of the references
     * between those stores, and the optimizations and constraints set on the schema.
     *
     * @return schema description
     */
    public static IDatastoreSchemaDescription schemaDescription() {

        final Collection<IStoreDescription> stores = new LinkedList<>();
        stores.add(createPositionsStoreDescription());
        stores.add(createInstrumentsStoreDescription());

        return new DatastoreSchemaDescription(stores, references());
    }
}
