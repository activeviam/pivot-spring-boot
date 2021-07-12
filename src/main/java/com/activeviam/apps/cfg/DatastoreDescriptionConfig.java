/*
 * (C) ActiveViam 2021
 * ALL RIGHTS RESERVED. This material is the CONFIDENTIAL and PROPRIETARY
 * property of ActiveViam. Any unauthorized use,
 * reproduction or transfer of this material is strictly prohibited
 */

package com.activeviam.apps.cfg;

import static com.activeviam.apps.constants.StoreAndFieldConstants.BENCHMARK_DATA_ID;
import static com.activeviam.apps.constants.StoreAndFieldConstants.BENCHMARK_DATA_STORE_NAME;
import static com.activeviam.apps.constants.StoreAndFieldConstants.BENCHMARK_INFO_DESCRIPTION;
import static com.activeviam.apps.constants.StoreAndFieldConstants.BENCHMARK_INFO_PARAMETER_NAMES;
import static com.activeviam.apps.constants.StoreAndFieldConstants.BENCHMARK_INFO_PLUGIN_KEY;
import static com.activeviam.apps.constants.StoreAndFieldConstants.BENCHMARK_INFO_STORE_NAME;
import static com.activeviam.apps.constants.StoreAndFieldConstants.BENCHMARK_INFO_VALUES_NAMES;
import static com.activeviam.apps.constants.StoreAndFieldConstants.BENCHMARK_P1;
import static com.activeviam.apps.constants.StoreAndFieldConstants.BENCHMARK_P2;
import static com.activeviam.apps.constants.StoreAndFieldConstants.BENCHMARK_P3;
import static com.activeviam.apps.constants.StoreAndFieldConstants.BENCHMARK_P4;
import static com.activeviam.apps.constants.StoreAndFieldConstants.BENCHMARK_P5;
import static com.activeviam.apps.constants.StoreAndFieldConstants.BENCHMARK_V1;
import static com.activeviam.apps.constants.StoreAndFieldConstants.BENCHMARK_V2;
import static com.activeviam.apps.constants.StoreAndFieldConstants.BENCHMARK_V3;
import static com.activeviam.apps.constants.StoreAndFieldConstants.BENCHMARK_V4;
import static com.activeviam.apps.constants.StoreAndFieldConstants.BENCHMARK_V5;
import static com.activeviam.apps.constants.StoreAndFieldConstants.SESSION_BENCHMARKS_BENCHMARK_KEY;
import static com.activeviam.apps.constants.StoreAndFieldConstants.SESSION_BENCHMARKS_BENCHMARK_NAME;
import static com.activeviam.apps.constants.StoreAndFieldConstants.SESSION_BENCHMARKS_ID;
import static com.activeviam.apps.constants.StoreAndFieldConstants.SESSION_BENCHMARKS_SESSION_ID;
import static com.activeviam.apps.constants.StoreAndFieldConstants.SESSION_BENCHMARKS_STORE_NAME;
import static com.activeviam.apps.constants.StoreAndFieldConstants.SESSION_BRANCH;
import static com.activeviam.apps.constants.StoreAndFieldConstants.SESSION_COMMIT_SHA1;
import static com.activeviam.apps.constants.StoreAndFieldConstants.SESSION_ID;
import static com.activeviam.apps.constants.StoreAndFieldConstants.SESSION_INFO_CPU;
import static com.activeviam.apps.constants.StoreAndFieldConstants.SESSION_INFO_DIRECT;
import static com.activeviam.apps.constants.StoreAndFieldConstants.SESSION_INFO_HEAP;
import static com.activeviam.apps.constants.StoreAndFieldConstants.SESSION_INFO_ID;
import static com.activeviam.apps.constants.StoreAndFieldConstants.SESSION_INFO_JAVA_VERSION;
import static com.activeviam.apps.constants.StoreAndFieldConstants.SESSION_INFO_RAM_QTY;
import static com.activeviam.apps.constants.StoreAndFieldConstants.SESSION_INFO_STORE_NAME;
import static com.activeviam.apps.constants.StoreAndFieldConstants.SESSION_INSTANCE_ID;
import static com.activeviam.apps.constants.StoreAndFieldConstants.SESSION_STORE_NAME;
import static com.activeviam.apps.constants.StoreAndFieldConstants.SESSION_TAGS;
import static com.activeviam.apps.constants.StoreAndFieldConstants.SESSION_TIMESTAMP;

import com.qfs.desc.IDatastoreSchemaDescription;
import com.qfs.desc.IReferenceDescription;
import com.qfs.desc.IStoreDescription;
import com.qfs.desc.impl.DatastoreSchemaDescription;
import com.qfs.desc.impl.ReferenceDescription;
import com.qfs.desc.impl.StoreDescriptionBuilder;
import com.qfs.literal.ILiteralType;
import java.util.Collection;
import java.util.LinkedList;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DatastoreDescriptionConfig {


    public static IStoreDescription createSessionsStoreDescription() {
        return new StoreDescriptionBuilder().withStoreName(SESSION_STORE_NAME)
            .withField(SESSION_ID, ILiteralType.LONG).asKeyField()
            .withField(SESSION_TIMESTAMP,ILiteralType.LONG)
            .withField(SESSION_TAGS,ILiteralType.STRING_ARRAY,new String[]{})
            .withField(SESSION_BRANCH,ILiteralType.STRING)
            .withField(SESSION_COMMIT_SHA1,ILiteralType.STRING)
            .withField(SESSION_INSTANCE_ID,ILiteralType.STRING).build();
    }

    public static IStoreDescription createSessionInfoStoreDescription() {
        return new StoreDescriptionBuilder().withStoreName(SESSION_INFO_STORE_NAME)
            .withField(SESSION_INFO_ID,ILiteralType.LONG).asKeyField()
            .withField(SESSION_INFO_CPU,ILiteralType.STRING)
            .withField(SESSION_INFO_HEAP,ILiteralType.LONG)
            .withField(SESSION_INFO_DIRECT,ILiteralType.LONG)
            .withField(SESSION_INFO_RAM_QTY,ILiteralType.LONG)
            .withField(SESSION_INFO_JAVA_VERSION,ILiteralType.STRING).build();
    }

    public static IStoreDescription createSessionBenchmarksStoreDescription() {
        return new StoreDescriptionBuilder().withStoreName(SESSION_BENCHMARKS_STORE_NAME)
            .withField(SESSION_BENCHMARKS_ID,ILiteralType.LONG).asKeyField()
            .withField(SESSION_BENCHMARKS_SESSION_ID,ILiteralType.LONG)
            .withField(SESSION_BENCHMARKS_BENCHMARK_NAME,ILiteralType.STRING)
            .withField(SESSION_BENCHMARKS_BENCHMARK_KEY,ILiteralType.STRING).build();
    }

    public static IStoreDescription createBenchmarkInfoStoreDescription() {
        return new StoreDescriptionBuilder().withStoreName(BENCHMARK_INFO_STORE_NAME)
            .withField(BENCHMARK_INFO_PLUGIN_KEY,ILiteralType.STRING).asKeyField()
            .withField(BENCHMARK_INFO_PARAMETER_NAMES,ILiteralType.STRING_ARRAY,new String[]{})
            .withField(BENCHMARK_INFO_VALUES_NAMES,ILiteralType.STRING_ARRAY,new String[]{})
            .withField(BENCHMARK_INFO_DESCRIPTION,ILiteralType.STRING).build();
    }

    public static IStoreDescription createBenchmarkDataStore() {
        return new StoreDescriptionBuilder().withStoreName(BENCHMARK_DATA_STORE_NAME)
            .withField(BENCHMARK_DATA_ID,ILiteralType.LONG).asKeyField()
            .withField(BENCHMARK_P1,ILiteralType.STRING)
            .withField(BENCHMARK_P2,ILiteralType.STRING)
            .withField(BENCHMARK_P3,ILiteralType.STRING)
            .withField(BENCHMARK_P4,ILiteralType.STRING)
            .withField(BENCHMARK_P5,ILiteralType.STRING)
            .withField(BENCHMARK_V1,ILiteralType.STRING)
            .withField(BENCHMARK_V2,ILiteralType.STRING)
            .withField(BENCHMARK_V3,ILiteralType.STRING)
            .withField(BENCHMARK_V4,ILiteralType.STRING)
            .withField(BENCHMARK_V5,ILiteralType.STRING).build();
    }

    public static Collection<IReferenceDescription> references() {
        final Collection<IReferenceDescription> references = new LinkedList<>();
        references.add(ReferenceDescription.builder()
            .fromStore(SESSION_STORE_NAME)
            .toStore(SESSION_INFO_STORE_NAME)
            .withName(
                generateReferenceName(SESSION_STORE_NAME,SESSION_INFO_STORE_NAME))
            .withMapping(SESSION_ID,SESSION_INFO_ID)
            .build());

        references.add(ReferenceDescription.builder()
            .fromStore(SESSION_STORE_NAME)
            .toStore(SESSION_BENCHMARKS_STORE_NAME)
            .withName(
                generateReferenceName(SESSION_STORE_NAME,SESSION_BENCHMARKS_STORE_NAME))
            .withMapping(SESSION_ID,SESSION_BENCHMARKS_SESSION_ID)
            .build());

        references.add(ReferenceDescription.builder()
            .fromStore(SESSION_BENCHMARKS_STORE_NAME)
            .toStore(BENCHMARK_INFO_STORE_NAME)
            .withName(
                generateReferenceName(SESSION_BENCHMARKS_STORE_NAME,BENCHMARK_INFO_STORE_NAME))
            .withMapping(SESSION_BENCHMARKS_BENCHMARK_KEY,BENCHMARK_INFO_PLUGIN_KEY)
            .build());

        references.add(ReferenceDescription.builder()
            .fromStore(SESSION_BENCHMARKS_STORE_NAME)
            .toStore(BENCHMARK_DATA_STORE_NAME)
            .withName(
                generateReferenceName(SESSION_BENCHMARKS_STORE_NAME,BENCHMARK_DATA_STORE_NAME))
            .withMapping(SESSION_BENCHMARKS_ID,BENCHMARK_DATA_ID)
            .build());
        return references;
    }

    private static String generateReferenceName(String fromStore, String toStore) {
        return fromStore + "To" + Character.toUpperCase(toStore.charAt(0)) + toStore.substring(1);
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
        stores.add(createSessionsStoreDescription());
        stores.add(createSessionInfoStoreDescription());
        stores.add(createSessionBenchmarksStoreDescription());
        stores.add(createBenchmarkInfoStoreDescription());
        stores.add(createBenchmarkDataStore());

        return new DatastoreSchemaDescription(stores, references());
    }
}
