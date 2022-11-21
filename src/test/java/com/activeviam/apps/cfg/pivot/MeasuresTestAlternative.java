package com.activeviam.apps.cfg.pivot;

import com.activeviam.apps.activepivot.data.datastore.DatastoreConfigurer;
import com.activeviam.apps.activepivot.pivot.DimensionsConfigurer;
import com.activeviam.apps.activepivot.pivot.MeasuresConfigurer;
import com.activeviam.apps.activepivot.pivot.SchemaSelectionConfigurer;
import com.activeviam.builders.StartBuilding;
import com.activeviam.copper.CopperRegistrations;
import com.activeviam.copper.builders.ITransactionsBuilder;
import com.activeviam.copper.builders.impl.SimpleTransactionBuilder;
import com.activeviam.copper.testing.CubeTester;
import com.activeviam.copper.testing.CubeTesterBuilder;
import com.activeviam.copper.testing.CubeTesterBuilderExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.time.LocalDate;

import static com.activeviam.apps.activepivot.data.datastore.StoreAndFieldConstants.TRADES_NOTIONAL;
import static com.activeviam.apps.activepivot.data.datastore.StoreAndFieldConstants.TRADES_STORE_NAME;
import static com.activeviam.apps.activepivot.pivot.CubeConstants.CUBE_NAME;

@SpringJUnitConfig
class MeasuresTestAlternative {

    @TestConfiguration
    @Import(value = {DatastoreConfigurer.class, SchemaSelectionConfigurer.class, DimensionsConfigurer.class, MeasuresConfigurer.class})
    public static class MeasuresTestAlternativeConfiguration {

        static {
            CopperRegistrations.setupRegistryForTests();
        }
    }

    @Autowired
    private SchemaSelectionConfigurer selectionConfigurer;

    @Autowired
    private DimensionsConfigurer dimensionsConfigurer;

    @Autowired
    private DatastoreConfigurer datastoreConfigurer;

    /**
     * NOTE: by registering the CubeTesterBuilderExtension as an extension, we let it create and destroy the test cube
     * before/after each test
     * (see {@link CubeTesterBuilderExtension#beforeEach(ExtensionContext)}
     * and {@link CubeTesterBuilderExtension#afterEach(ExtensionContext)})
     * This is useful if we are adding different data between tests, or if we modify the data inside a tests. If the
     * data is unchanged, we can avoid recreating the cube, see {@link MeasuresTest}
     */
    @RegisterExtension
    public CubeTesterBuilderExtension builder = new CubeTesterBuilderExtension(testerBuilder());

    private CubeTester tester;

    // This could also be done inside each test if we want to add different measures or different data in each test!
    @BeforeEach
    public void createTester(MeasuresConfigurer measuresConfigurer) {
        tester = builder
                // we could add different data here if we wanted!
                .setData(TestUtils.createTestData())
                .build(measuresConfigurer::add);
    }

    /**
     * Creates the tester using the descriptions of the project.
     *
     * @return The tester.
     */
    private CubeTesterBuilder testerBuilder() {
        final var datastoreDescription = datastoreConfigurer.datastoreSchemaDescription();
        final var selectionDescription = selectionConfigurer.createSchemaSelectionDescription(datastoreDescription);
        final var cubeDescription = StartBuilding.cube()
                .withName(CUBE_NAME)
                .withDimensions(dimensionsConfigurer::add)
                .build();
        return new CubeTesterBuilder(datastoreDescription, selectionDescription, cubeDescription);
    }

    /**
     * Here is the actual test. Check that the numbers sum up correctly
     */
    @Test
    void tradesNotionalTotal_test() {
        // We need to inject the
        tester.query()
                .forMeasures(TRADES_NOTIONAL)
                .run()
                .getTester()
                .hasOnlyOneCellWith()
                .containing(750d);
    }

    /**
     * Run a test on an MDX query with a slicer
     */
    @Test
    void tradesNotionalTotal_withSlicer_test() {
        tester.mdxQuery("SELECT" + "  [Measures].[Notional] ON COLUMNS"
                        + "  FROM [Cube]"
                        + "  WHERE [TradeID].[TradeID].[AllMember].[T1]")
                .getTester()
                .hasOnlyOneCell()
                .containingFormattedValue("100");
    }
}
