/*
 * (C) ActiveViam 2021
 * ALL RIGHTS RESERVED. This material is the CONFIDENTIAL and PROPRIETARY
 * property of ActiveViam. Any unauthorized use,
 * reproduction or transfer of this material is strictly prohibited
 */

package com.activeviam.apps.cfg;

import com.activeviam.apps.constants.StoreAndFieldConstants;
import com.qfs.desc.IDatastoreSchemaDescription;
import com.qfs.desc.IReferenceDescription;
import com.qfs.desc.IStoreDescription;
import com.qfs.desc.impl.DatastoreSchemaDescription;
import com.qfs.desc.impl.StoreDescriptionBuilder;
import com.qfs.literal.ILiteralType;
import org.springframework.context.annotation.Configuration;

import java.util.Collection;
import java.util.LinkedList;

@Configuration
public class DatastoreDescriptionConfig {


    public static IStoreDescription createTradesStoreDescription() {
        return new StoreDescriptionBuilder().withStoreName(StoreAndFieldConstants.SESSION_STORE_NAME)
            .withField(StoreAndFieldConstants.SESSION_TIMESTAMP, ILiteralType.LONG).asKeyField()
            .withNullableField(StoreAndFieldConstants.SESSION_TAGS, ILiteralType.STRING_ARRAY)
            .withField(StoreAndFieldConstants.SESSION_CPU,ILiteralType.STRING)
            .withField(StoreAndFieldConstants.SESSION_RAM_QTY)
            .withField(StoreAndFieldConstants.SESSION_HEAP,ILiteralType.STRING)
            .withField(StoreAndFieldConstants.SESSION_DIRECT,ILiteralType.STRING)
            .withField(StoreAndFieldConstants.SESSION_JAVA_VERSION,ILiteralType.STRING)
            .build();
    }

    public static Collection<IReferenceDescription> references() {
        final Collection<IReferenceDescription> references = new LinkedList<>();
        return references;
    }

    /**
     * Provide the schema description of the datastore.
     * <p>
     * It is based on the descriptions of the stores in the datastore, the descriptions of the
     * references between those stores, and the optimizations and constraints set on the schema.
     *
     * @return schema description
     */
    public static IDatastoreSchemaDescription schemaDescription() {

        final Collection<IStoreDescription> stores = new LinkedList<>();
        stores.add(createTradesStoreDescription());

        return new DatastoreSchemaDescription(stores, references());
    }
}
