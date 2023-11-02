package com.activeviam.apps.cfg;

import com.activeviam.apps.constants.StoreAndFieldConstants;
import com.qfs.desc.IDatastoreSchemaDescription;
import com.qfs.desc.IReferenceDescription;
import com.qfs.desc.IStoreDescription;
import com.qfs.desc.impl.DatastoreSchemaDescription;
import com.qfs.desc.impl.StoreDescriptionBuilder;
import org.springframework.context.annotation.Configuration;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import static com.qfs.literal.ILiteralType.*;

@Configuration
public class DatastoreDescriptionConfig {

    public static IStoreDescription createTradesStoreDescription() {
        return new StoreDescriptionBuilder().withStoreName(StoreAndFieldConstants.TRADES_STORE_NAME)
                .withField(StoreAndFieldConstants.AS_OF_DATE, LOCAL_DATE).asKeyField()
                .withField(StoreAndFieldConstants.TRADE_ID, STRING).asKeyField()
                .withField(StoreAndFieldConstants.NOTIONAL, DOUBLE)
                .build();
    }

    public static Collection<IReferenceDescription> references() {
        return List.of();
    }

    /**
     * Provide the schema description of the datastore.
     * <p>
     * It is based on the descriptions of the stores in the datastore, the descriptions of the references
     * between those stores, and the optimizations and constraints set on the schema.
     *
     * @return schema description
     */
    public static IDatastoreSchemaDescription schemaDescription() {

        return new DatastoreSchemaDescription(List.of(createTradesStoreDescription()), references());
    }
}