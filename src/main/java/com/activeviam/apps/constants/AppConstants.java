package com.activeviam.apps.constants;

public class AppConstants {

    /*********************** Stores names **********************/
    public static final String SESSION_STORE_NAME = "Session";
    public static final String SESSION_BENCHMARKS_STORE_NAME = "Session_Benchmarks";
    public static final String SESSION_INFO_STORE_NAME = "Session_Info";
    public static final String BENCHMARK_INFO_STORE_NAME = "Benchmarks_Info";
    public static final String BENCHMARK_DATA_STORE_NAME = "Benchmark_Data";

    /********************* Stores fields ***********************/

    /** Session store */
    public static final String SESSION_ID = "Session ID";
    public static final String SESSION_TIMESTAMP = "Timestamp";
    public static final String SESSION_TAGS = "Executed Tags";
    public static final String SESSION_AP_VERSION = "Version";
    public static final String SESSION_BRANCH = "Branch";
    public static final String SESSION_COMMIT_SHA1 = "Commit SHA1";
    public static final String SESSION_INSTANCE_ID = "Execution Instance Identifier";
    public static final String SESSION_HOSTNAME = "Hostname Identifier";

    /** Session info store */
    public static final String SESSION_INFO_ID = "Session ID";
    public static final String SESSION_INFO_CPU = "CPU";
    public static final String SESSION_INFO_RAM_QTY = "Total Machine RAM quantity";
    public static final String SESSION_INFO_HEAP = "VM Heap RAM";
    public static final String SESSION_INFO_DIRECT = "VM Direct RAM";
    public static final String SESSION_INFO_JAVA_VERSION = "Java Version";

    /** Session Benchmarks store */
    public static final String SESSION_BENCHMARKS_ID = "Benchmark ID";
    public static final String SESSION_BENCHMARKS_SESSION_ID = "Session ID";
    public static final String SESSION_BENCHMARKS_BENCHMARK_NAME = "Benchmark Name";
    public static final String SESSION_BENCHMARKS_BENCHMARK_KEY = "Benchmark Plugin key";

    /** Benchmark info store */
    public static final String BENCHMARK_INFO_PLUGIN_KEY = "Plugin key";
    public static final String BENCHMARKS_INFO_TAG = "Benchmark tag";
    public static final String BENCHMARK_INFO_PARAMETER_NAMES = "Parameter names";
    public static final String BENCHMARK_INFO_VALUES_NAMES = "Values names";
    public static final String BENCHMARK_INFO_METRICS_UNITS_NAMES = "Values unit names";
    public static final String BENCHMARK_INFO_DESCRIPTION = "Benchmark description";

    /** Benchmark data store */
    public static final String BENCHMARK_DATA_ID = "BenchmarkID";
    public static final String BENCHMARK_P1 = "P1";
    public static final String BENCHMARK_P2 = "P2";
    public static final String BENCHMARK_P3 = "P3";
    public static final String BENCHMARK_P4 = "P4";
    public static final String BENCHMARK_P5 = "P5";
    public static final String BENCHMARK_P6 = "P6";
    public static final String BENCHMARK_P7 = "P7";
    public static final String BENCHMARK_P8 = "P8";
    public static final String BENCHMARK_V1 = "V1";
    public static final String BENCHMARK_V2 = "V2";
    public static final String BENCHMARK_V3 = "V3";
    public static final String BENCHMARK_V4 = "V4";
    public static final String BENCHMARK_V5 = "V5";
    public static final String BENCHMARK_V6 = "V6";
    public static final String BENCHMARK_V7 = "V7";
    public static final String BENCHMARK_V8 = "V8";
    public static final String BENCHMARK_V9 = "V9";
    public static final String BENCHMARK_V10 = "V10";
    public static final String BENCHMARK_V1_E = "V1_E";
    public static final String BENCHMARK_V2_E = "V2_E";
    public static final String BENCHMARK_V3_E = "V3_E";
    public static final String BENCHMARK_V4_E = "V4_E";
    public static final String BENCHMARK_V5_E = "V5_E";
    public static final String BENCHMARK_V6_E = "V6_E";
    public static final String BENCHMARK_V7_E = "V7_E";
    public static final String BENCHMARK_V8_E = "V8_E";
    public static final String BENCHMARK_V9_E = "V9_E";
    public static final String BENCHMARK_V10_E = "V10_E";

    //-------------------------------------------------//
    //                 CUBE CONSTANTS                  //
    //-------------------------------------------------//

    // Dimension names
    public static final String SESSION_DIMENSION = "Session";
    public static final String BENCHMARK_DIMENSION = "Benchmark";
    public static final String BENCHMARK_PARAMETERS_DIMENSION = "Parameters";

    // Hierarchies
    public static final String CODE_VERSION_HIERARCHY = "Code Version";
    public static final String TIME_HIERARCHY = "Execution time";
    public static final String BENCHMARK_HIERARCHY = "Benchmark";


    // Levels
    public static final String TIMESTAMP_LEVEL= SESSION_TIMESTAMP;

    public static final String AP_VERSION_LEVEL = SESSION_AP_VERSION;
    public static final String BRANCH_NAME_LEVEL = SESSION_BRANCH;
    public static final String SHA1_LEVEL = SESSION_COMMIT_SHA1;
    public static final String SESSION_ID_LEVEL = SESSION_ID;

    public static final String BENCHMARK_TAGS_LEVEL = BENCHMARKS_INFO_TAG;
    public static final String BENCHMARK_KEY_LEVEL = BENCHMARK_INFO_PLUGIN_KEY;

    public static final String BENCHMARK_P1_LEVEL = BENCHMARK_P1;
    public static final String BENCHMARK_P2_LEVEL = BENCHMARK_P2;
    public static final String BENCHMARK_P3_LEVEL = BENCHMARK_P3;
    public static final String BENCHMARK_P4_LEVEL = BENCHMARK_P4;
    public static final String BENCHMARK_P5_LEVEL = BENCHMARK_P5;
    public static final String BENCHMARK_P6_LEVEL = BENCHMARK_P6;
    public static final String BENCHMARK_P7_LEVEL = BENCHMARK_P7;
    public static final String BENCHMARK_P8_LEVEL = BENCHMARK_P8;
}
