/*
 * (C) ActiveViam 2021
 * ALL RIGHTS RESERVED. This material is the CONFIDENTIAL and PROPRIETARY
 * property of ActiveViam. Any unauthorized use,
 * reproduction or transfer of this material is strictly prohibited
 */

package com.activeviam.apps.cfg;

import static com.activeviam.apps.constants.AppConstants.BENCHMARKS_INFO_TAG;
import static com.activeviam.apps.constants.AppConstants.BENCHMARK_DATA_ID;
import static com.activeviam.apps.constants.AppConstants.BENCHMARK_DATA_STORE_NAME;
import static com.activeviam.apps.constants.AppConstants.BENCHMARK_INFO_DESCRIPTION;
import static com.activeviam.apps.constants.AppConstants.BENCHMARK_INFO_METRICS_UNITS_NAMES;
import static com.activeviam.apps.constants.AppConstants.BENCHMARK_INFO_PARAMETER_NAMES;
import static com.activeviam.apps.constants.AppConstants.BENCHMARK_INFO_PLUGIN_KEY;
import static com.activeviam.apps.constants.AppConstants.BENCHMARK_INFO_STORE_NAME;
import static com.activeviam.apps.constants.AppConstants.BENCHMARK_INFO_VALUES_NAMES;
import static com.activeviam.apps.constants.AppConstants.BENCHMARK_P1;
import static com.activeviam.apps.constants.AppConstants.BENCHMARK_P2;
import static com.activeviam.apps.constants.AppConstants.BENCHMARK_P3;
import static com.activeviam.apps.constants.AppConstants.BENCHMARK_P4;
import static com.activeviam.apps.constants.AppConstants.BENCHMARK_P5;
import static com.activeviam.apps.constants.AppConstants.BENCHMARK_P6;
import static com.activeviam.apps.constants.AppConstants.BENCHMARK_P7;
import static com.activeviam.apps.constants.AppConstants.BENCHMARK_P8;
import static com.activeviam.apps.constants.AppConstants.BENCHMARK_V1;
import static com.activeviam.apps.constants.AppConstants.BENCHMARK_V10;
import static com.activeviam.apps.constants.AppConstants.BENCHMARK_V10_E;
import static com.activeviam.apps.constants.AppConstants.BENCHMARK_V1_E;
import static com.activeviam.apps.constants.AppConstants.BENCHMARK_V2;
import static com.activeviam.apps.constants.AppConstants.BENCHMARK_V2_E;
import static com.activeviam.apps.constants.AppConstants.BENCHMARK_V3;
import static com.activeviam.apps.constants.AppConstants.BENCHMARK_V3_E;
import static com.activeviam.apps.constants.AppConstants.BENCHMARK_V4;
import static com.activeviam.apps.constants.AppConstants.BENCHMARK_V4_E;
import static com.activeviam.apps.constants.AppConstants.BENCHMARK_V5;
import static com.activeviam.apps.constants.AppConstants.BENCHMARK_V5_E;
import static com.activeviam.apps.constants.AppConstants.BENCHMARK_V6;
import static com.activeviam.apps.constants.AppConstants.BENCHMARK_V6_E;
import static com.activeviam.apps.constants.AppConstants.BENCHMARK_V7;
import static com.activeviam.apps.constants.AppConstants.BENCHMARK_V7_E;
import static com.activeviam.apps.constants.AppConstants.BENCHMARK_V8;
import static com.activeviam.apps.constants.AppConstants.BENCHMARK_V8_E;
import static com.activeviam.apps.constants.AppConstants.BENCHMARK_V9;
import static com.activeviam.apps.constants.AppConstants.BENCHMARK_V9_E;
import static com.activeviam.apps.constants.AppConstants.SESSION_AP_VERSION;
import static com.activeviam.apps.constants.AppConstants.SESSION_BENCHMARKS_BENCHMARK_KEY;
import static com.activeviam.apps.constants.AppConstants.SESSION_BENCHMARKS_BENCHMARK_NAME;
import static com.activeviam.apps.constants.AppConstants.SESSION_BENCHMARKS_ID;
import static com.activeviam.apps.constants.AppConstants.SESSION_BENCHMARKS_SESSION_ID;
import static com.activeviam.apps.constants.AppConstants.SESSION_BENCHMARKS_STORE_NAME;
import static com.activeviam.apps.constants.AppConstants.SESSION_BRANCH;
import static com.activeviam.apps.constants.AppConstants.SESSION_COMMIT_SHA1;
import static com.activeviam.apps.constants.AppConstants.SESSION_HOSTNAME;
import static com.activeviam.apps.constants.AppConstants.SESSION_ID;
import static com.activeviam.apps.constants.AppConstants.SESSION_INFO_CPU;
import static com.activeviam.apps.constants.AppConstants.SESSION_INFO_DIRECT;
import static com.activeviam.apps.constants.AppConstants.SESSION_INFO_HEAP;
import static com.activeviam.apps.constants.AppConstants.SESSION_INFO_ID;
import static com.activeviam.apps.constants.AppConstants.SESSION_INFO_JAVA_VERSION;
import static com.activeviam.apps.constants.AppConstants.SESSION_INFO_RAM_QTY;
import static com.activeviam.apps.constants.AppConstants.SESSION_INFO_STORE_NAME;
import static com.activeviam.apps.constants.AppConstants.SESSION_INSTANCE_ID;
import static com.activeviam.apps.constants.AppConstants.SESSION_STORE_NAME;
import static com.activeviam.apps.constants.AppConstants.SESSION_TAGS;
import static com.activeviam.apps.constants.AppConstants.SESSION_TIMESTAMP;

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
            .withField(SESSION_ID, ILiteralType.STRING).asKeyField()
            .withField(SESSION_TIMESTAMP,ILiteralType.LONG)
            .withField(SESSION_TAGS,ILiteralType.STRING_ARRAY,new String[]{})
            .withField(SESSION_AP_VERSION,ILiteralType.STRING)
            .withField(SESSION_BRANCH,ILiteralType.STRING)
            .withField(SESSION_COMMIT_SHA1,ILiteralType.STRING)
            .withField(SESSION_INSTANCE_ID,ILiteralType.STRING)
            .withField(SESSION_HOSTNAME).build();
    }

    public static IStoreDescription createSessionInfoStoreDescription() {
        return new StoreDescriptionBuilder().withStoreName(SESSION_INFO_STORE_NAME)
            .withField(SESSION_INFO_ID,ILiteralType.STRING).asKeyField()
            .withField(SESSION_INFO_CPU,ILiteralType.STRING)
            .withField(SESSION_INFO_HEAP,ILiteralType.STRING)
            .withField(SESSION_INFO_DIRECT,ILiteralType.STRING)
            .withField(SESSION_INFO_RAM_QTY,ILiteralType.STRING)
            .withField(SESSION_INFO_JAVA_VERSION,ILiteralType.STRING).build();
    }

    public static IStoreDescription createSessionBenchmarksStoreDescription() {
        return new StoreDescriptionBuilder().withStoreName(SESSION_BENCHMARKS_STORE_NAME)
            .withField(SESSION_BENCHMARKS_ID,ILiteralType.STRING).asKeyField()
            .withField(SESSION_BENCHMARKS_SESSION_ID,ILiteralType.STRING)
            .withField(SESSION_BENCHMARKS_BENCHMARK_NAME,ILiteralType.STRING)
            .withField(SESSION_BENCHMARKS_BENCHMARK_KEY,ILiteralType.STRING).build();
    }

    public static IStoreDescription createBenchmarkInfoStoreDescription() {
        return new StoreDescriptionBuilder().withStoreName(BENCHMARK_INFO_STORE_NAME)
            .withField(BENCHMARK_INFO_PLUGIN_KEY,ILiteralType.STRING).asKeyField()
            .withField(BENCHMARKS_INFO_TAG,ILiteralType.STRING_ARRAY,new String[]{})
            .withField(BENCHMARK_INFO_PARAMETER_NAMES,ILiteralType.STRING_ARRAY,new String[]{})
            .withField(BENCHMARK_INFO_VALUES_NAMES,ILiteralType.STRING_ARRAY,new String[]{})
            .withField(BENCHMARK_INFO_METRICS_UNITS_NAMES,ILiteralType.STRING_ARRAY,new String[]{})
            .withField(BENCHMARK_INFO_DESCRIPTION,ILiteralType.STRING).build();
    }

    public static IStoreDescription createBenchmarkDataStore() {
        return new StoreDescriptionBuilder().withStoreName(BENCHMARK_DATA_STORE_NAME)
            .withField(BENCHMARK_DATA_ID,ILiteralType.STRING).asKeyField()
            .withField(BENCHMARK_P1,ILiteralType.STRING).asKeyField()
            .withField(BENCHMARK_P2,ILiteralType.STRING).asKeyField()
            .withField(BENCHMARK_P3,ILiteralType.STRING).asKeyField()
            .withField(BENCHMARK_P4,ILiteralType.STRING).asKeyField()
            .withField(BENCHMARK_P5,ILiteralType.STRING).asKeyField()
            .withField(BENCHMARK_P6,ILiteralType.STRING).asKeyField()
            .withField(BENCHMARK_P7,ILiteralType.STRING).asKeyField()
            .withField(BENCHMARK_P8,ILiteralType.STRING).asKeyField()
            .withField(BENCHMARK_V1,ILiteralType.STRING)
            .withField(BENCHMARK_V2,ILiteralType.STRING)
            .withField(BENCHMARK_V3,ILiteralType.STRING)
            .withField(BENCHMARK_V4,ILiteralType.STRING)
            .withField(BENCHMARK_V5,ILiteralType.STRING)
            .withField(BENCHMARK_V6,ILiteralType.STRING)
            .withField(BENCHMARK_V7,ILiteralType.STRING)
            .withField(BENCHMARK_V8,ILiteralType.STRING)
            .withField(BENCHMARK_V9,ILiteralType.STRING)
            .withField(BENCHMARK_V10,ILiteralType.STRING)
            .withField(BENCHMARK_V1_E,ILiteralType.STRING)
            .withField(BENCHMARK_V2_E,ILiteralType.STRING)
            .withField(BENCHMARK_V3_E,ILiteralType.STRING)
            .withField(BENCHMARK_V4_E,ILiteralType.STRING)
            .withField(BENCHMARK_V5_E,ILiteralType.STRING)
            .withField(BENCHMARK_V6_E,ILiteralType.STRING)
            .withField(BENCHMARK_V7_E,ILiteralType.STRING)
            .withField(BENCHMARK_V8_E,ILiteralType.STRING)
            .withField(BENCHMARK_V9_E,ILiteralType.STRING)
            .withField(BENCHMARK_V10_E,ILiteralType.STRING).build();
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
            .fromStore(SESSION_BENCHMARKS_STORE_NAME)
            .toStore(SESSION_STORE_NAME)
            .withName(
                generateReferenceName(SESSION_BENCHMARKS_STORE_NAME,SESSION_STORE_NAME))
            .withMapping(SESSION_BENCHMARKS_SESSION_ID,SESSION_ID)
            .build());

        references.add(ReferenceDescription.builder()
            .fromStore(SESSION_BENCHMARKS_STORE_NAME)
            .toStore(BENCHMARK_INFO_STORE_NAME)
            .withName(
                generateReferenceName(SESSION_BENCHMARKS_STORE_NAME,BENCHMARK_INFO_STORE_NAME))
            .withMapping(SESSION_BENCHMARKS_BENCHMARK_KEY,BENCHMARK_INFO_PLUGIN_KEY)
            .build());

        references.add(ReferenceDescription.builder()
            .fromStore(BENCHMARK_DATA_STORE_NAME)
            .toStore(SESSION_BENCHMARKS_STORE_NAME)
            .withName(
                generateReferenceName(BENCHMARK_DATA_STORE_NAME,SESSION_BENCHMARKS_STORE_NAME))
            .withMapping(BENCHMARK_DATA_ID,SESSION_BENCHMARKS_ID)
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
