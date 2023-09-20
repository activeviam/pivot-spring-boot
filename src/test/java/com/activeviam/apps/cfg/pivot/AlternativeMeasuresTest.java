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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import com.activeviam.apps.cfg.DatastoreSchemaConfig;
import com.activeviam.apps.cfg.DatastoreSelectionConfig;
import com.activeviam.builders.StartBuilding;
import com.activeviam.copper.builders.ITransactionsBuilder;
import com.activeviam.copper.builders.impl.SimpleTransactionBuilder;
import com.activeviam.copper.testing.CubeTester;
import com.activeviam.copper.testing.CubeTesterBuilder;
import com.activeviam.copper.testing.CubeTesterBuilderExtension;

@SpringJUnitConfig
class AlternativeMeasuresTest {

    /**
     * NOTE: by registering the CubeTesterBuilderExtension as an extension, we let it create and destroy the test cube
     * before/after each test
     * (see {@link CubeTesterBuilderExtension#afterEach(ExtensionContext)})
     * This is useful if we are adding different data between tests, or if we modify the data inside a tests. If the
     * data is unchanged, we can avoid recreating the cube, see {@link MeasuresTest}
     */
    @RegisterExtension()
    public CubeTesterBuilderExtension builder = new CubeTesterBuilderExtension(AlternativeMeasuresTest::testerBuilder);

    @Autowired
    MeasureConfig measureConfig;

    private CubeTester tester;

    /**
     * Creates the tester using the descriptions of the project.
     *
     * @return The tester.
     */
    public static CubeTesterBuilder testerBuilder() {
        final var datastoreDescConfig = new DatastoreSchemaConfig();
        final var datastoreSchemaDesc = datastoreDescConfig.datastoreSchemaDescription();
        final var datastoreSelectionDesc = new DatastoreSelectionConfig(datastoreDescConfig);
        final var dimensionConfig = new DimensionConfig();
        final var cubeDescription = StartBuilding.cube()
                .withName("Cube")
                .withDimensions(dimensionConfig::build)
                .build();
        return new CubeTesterBuilder(
                datastoreSchemaDesc, datastoreSelectionDesc.createSchemaSelectionDescription(), cubeDescription);
    }

    public static ITransactionsBuilder createTestData() {
        return SimpleTransactionBuilder.start()
                .inStore(TRADES_STORE_NAME)
                .add(LocalDate.parse("2019-03-13"), "T1", 100d)
                .add(LocalDate.parse("2019-03-13"), "T2", 350d)
                .add(LocalDate.parse("2019-03-13"), "T3", 300d)
                .end();
    }

    // This could also be done inside each test if we want to add different measures or different data in each test!
    @BeforeEach
    public void createTester() {
        tester = builder
                // we could add different data here if we wanted!
                .setData(createTestData())
                .build(measureConfig::build);
    }

    /**
     * Here is the actual test. Check that the numbers sum up correctly
     */
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
                .getTester()
                .hasOnlyOneCellWith()
                .containing(1150d);
    }

    @TestConfiguration
    @Import({
        MeasureConfig.class,
    })
    public static class Configuration {
        // Mock beans and services here
    }
}
