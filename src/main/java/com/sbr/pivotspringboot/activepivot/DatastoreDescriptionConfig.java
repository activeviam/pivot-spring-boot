package com.sbr.pivotspringboot.activepivot;

import com.qfs.desc.IDatastoreSchemaDescription;
import com.qfs.desc.IDatastoreSchemaDescriptionPostProcessor;
import com.qfs.desc.IReferenceDescription;
import com.qfs.desc.IStoreDescription;
import com.qfs.desc.impl.DatastoreSchemaDescription;
import com.qfs.desc.impl.StoreDescriptionBuilder;
import com.qfs.multiversion.IEpochManagementPolicy;
import com.qfs.server.cfg.IDatastoreDescriptionConfig;
import org.springframework.context.annotation.Bean;

import java.util.Collection;
import java.util.LinkedList;

import static com.qfs.literal.ILiteralType.DOUBLE;
import static com.qfs.literal.ILiteralType.STRING;
import static com.qfs.literal.ILiteralType.LOCAL_DATE;
import static com.sbr.pivotspringboot.activepivot.StoreAndFieldConstants.*;

public class DatastoreDescriptionConfig implements IDatastoreDescriptionConfig {

    public static IStoreDescription createTradesStoreDescription() {
        return new StoreDescriptionBuilder().withStoreName(TRADES_STORE_NAME)
                .withField(ASOFDATE, LOCAL_DATE).asKeyField()
                .withField(TRADES__TRADEID, STRING).asKeyField()
                .withField(TRADES__NOTIONAL, DOUBLE)
                .onDuplicateKeyWithinTransaction().logException()
                .build();
    }

    public Collection<IReferenceDescription> references() {
        final Collection<IReferenceDescription> references = new LinkedList<>();
        return references;
    }

    /**
     *
     * Provide the schema description of the datastore.
     * <p>
     * It is based on the descriptions of the stores in the datastore, the descriptions of the
     * references between those stores, and the optimizations and constraints set on the schema.
     *
     * @return schema description
     */
    @Override
    @Bean
    public IDatastoreSchemaDescription schemaDescription() {

        final Collection<IStoreDescription> stores = new LinkedList<>();
        stores.add(createTradesStoreDescription());

        return new DatastoreSchemaDescription(stores, references());
    }
}
