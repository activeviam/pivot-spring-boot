/*
 * Copyright (C) ActiveViam 2023
 * ALL RIGHTS RESERVED. This material is the CONFIDENTIAL and PROPRIETARY
 * property of ActiveViam Limited. Any unauthorized use,
 * reproduction or transfer of this material is strictly prohibited
 */
package com.activeviam.apps.cfg.pivot;

import static com.activeviam.apps.constants.StoreAndFieldConstants.TRADES_NOTIONAL;
import static com.activeviam.apps.constants.StoreAndFieldConstants.TRADES_STORE_NAME;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import com.activeviam.apps.cfg.datastore.DatastoreSchemaConfig;
import com.activeviam.apps.cfg.DatabaseSelectionConfig;
import com.activeviam.builders.StartBuilding;
import com.activeviam.copper.CopperRegistrations;
import com.activeviam.copper.builders.ITransactionsBuilder;
import com.activeviam.copper.builders.impl.SimpleTransactionBuilder;
import com.activeviam.copper.testing.CubeTester;
import com.activeviam.copper.testing.CubeTesterBuilder;
import com.activeviam.copper.testing.CubeTesterBuilderExtension;
import com.quartetfs.biz.pivot.cube.hierarchy.measures.IMeasureHierarchy;

@SpringJUnitConfig
class MeasuresTest {

    @Autowired
    CubeTester tester;

    // @BeforeEach

    /**
     * Here is the actual test. Check that the numbers sum up correctly
     */
    @Test
    void countTest() {
        tester.query().forMeasures(IMeasureHierarchy.COUNT_ID).run().getTester().printCellSet();
    }

    @Test
    void countTestMDX() {
        tester.mdxQuery("SELECT" + "  [Measures].[contributors.COUNT] ON COLUMNS" + "  FROM [Cube]")
                .getTester()
                .printCellSet();
    }

    @Test
    void tradesNotionalTotal_test() {
        SimpleTransactionBuilder.start()
                .inStore(TRADES_STORE_NAME)
                .add(LocalDate.parse("2019-03-13"), "T4", 400d)
                .end()
                .feedInto(tester.datastore());
        tester.query()
                .forMeasures(TRADES_NOTIONAL)
                .run()
                .show()
                .getTester()
                .hasOnlyOneCellWith()
                .containing(1150d);
    }

    /**
     * NOTE: in this test, we setup the cube and load the data once. We then create a Tester as a bean and reuse it
     * for each test.
     * Also note we are not registering the CubeTesterBuilderExtension as an actual Extension (using @RegisterExtension)
     * because we don't want it to create and destroy the test cube before each test
     * (see {@link CubeTesterBuilderExtension#afterEach(ExtensionContext)})
     * <p>
     * If for some reason we need to recreate the cube before each test (because e.g. the data has been modified in
     * the test), then it is better to use the approach in {@link AlternativeMeasuresTest}
     */
    @TestConfiguration
    @Import({
        DatastoreSchemaConfig.class,
        DatabaseSelectionConfig.class,
        DimensionConfig.class,
        MeasureConfig.class,
    })
    public static class Configuration {

        // Add customer plugins (e.g. PostProcessors etc) here if needed!
        static {
            CopperRegistrations.setupRegistryForTests();
        }

        // Mock beans and services here

        public static ITransactionsBuilder createTestData() {
            return SimpleTransactionBuilder.start()
                    .inStore(TRADES_STORE_NAME)
                    .add(LocalDate.parse("2019-03-13"), "T1", 100d)
                    .add(LocalDate.parse("2019-03-13"), "T2", 350d)
                    .add(LocalDate.parse("2019-03-13"), "T3", 300d)
                    .end();
        }

        /**
         * Creates the tester using the descriptions of the project.
         *
         * @return The tester.
         */
        @Bean
        CubeTesterBuilder testerBuilder(
                DatastoreSchemaConfig datastoreSchemaConfig,
                DatabaseSelectionConfig databaseSelectionConfig,
                DimensionConfig dimensionConfig) {
            final var datastoreDescription = datastoreSchemaConfig.datastoreSchemaDescription();
            final var selectionDescription = databaseSelectionConfig.createSchemaSelectionDescription();
            final var cubeDescription = StartBuilding.cube()
                    .withName("Cube")
                    .withDimensions(dimensionConfig::build)
                    .build();
            return new CubeTesterBuilder(datastoreDescription, selectionDescription, cubeDescription);
        }

        @Bean
        public CubeTesterBuilderExtension cubeTesterBuilderExtension(CubeTesterBuilder cubeTesterBuilder) {
            return new CubeTesterBuilderExtension(() -> cubeTesterBuilder);
        }

        @Bean
        public CubeTester createTester(
                CubeTesterBuilderExtension cubeTesterBuilderExtension, MeasureConfig measureConfig) {
            return cubeTesterBuilderExtension.setData(createTestData()).build(measureConfig::build);
        }
    }
}
