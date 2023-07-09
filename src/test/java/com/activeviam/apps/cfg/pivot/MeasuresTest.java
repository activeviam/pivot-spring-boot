package com.activeviam.apps.cfg.pivot;

import static com.activeviam.apps.constants.StoreAndFieldConstants.TRADES_NOTIONAL;
import static com.activeviam.apps.constants.StoreAndFieldConstants.TRADES_STORE_NAME;

import com.activeviam.apps.cfg.DatastoreDescriptionConfig;
import com.activeviam.builders.StartBuilding;
import com.activeviam.copper.CopperRegistrations;
import com.activeviam.copper.builders.ITransactionsBuilder;
import com.activeviam.copper.builders.impl.SimpleTransactionBuilder;
import com.activeviam.copper.testing.CubeTesterBuilder;
import com.activeviam.copper.testing.CubeTesterBuilderExtension;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

class MeasuresTest {

    @BeforeAll
    public static void setup() {
        CopperRegistrations.setupRegistryForTests();
    }

    @RegisterExtension
    public CubeTesterBuilderExtension builder = new CubeTesterBuilderExtension(MeasuresTest::createTester);

    /**
     * Creates the tester using the descriptions of the project.
     *
     * @return The tester.
     */
    public static CubeTesterBuilder createTester() {
        final var measureConfig = new MeasureConfig();
        final var cubeConfig = new CubeConfig(measureConfig);
        final var datastoreDescConfig = new DatastoreDescriptionConfig();
        final var datastoreDescription = datastoreDescConfig.datastoreSchemaDescription();
        final var pivotmanagerConfig = new PivotManagerConfig(datastoreDescConfig, cubeConfig);
        final var selectionDescription = pivotmanagerConfig.createSchemaSelectionDescription();

        final var cubeDescription = StartBuilding.cube()
                .withName("Cube")
                .withDimensions(cubeConfig::dimensions)
                .build();
        return new CubeTesterBuilder(datastoreDescription, selectionDescription, createTestData(), cubeDescription);
    }

    public static ITransactionsBuilder createTestData() {
        return SimpleTransactionBuilder.start()
                .inStore(TRADES_STORE_NAME)
                .add(LocalDate.parse("2019-03-13"), "T1", 100d)
                .add(LocalDate.parse("2019-03-13"), "T2", 350d)
                .add(LocalDate.parse("2019-03-13"), "T3", 300d)
                .end();
    }

    /**
     * Here is the actual test. Check that the numbers sum up correctly
     */
    @Test
    void testSimpleSum() {
        final var measureConfig = new MeasureConfig();
        final var tester = builder.build(measureConfig::build);
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
    void testSimpleSum_withSlicer() {
        final var measureConfig = new MeasureConfig();
        final var tester = builder.build(measureConfig::build);
        tester.mdxQuery("SELECT" + "  [Measures].[Notional] ON COLUMNS"
                        + "  FROM [Cube]"
                        + "  WHERE [TradeID].[TradeID].[ALL].[AllMember].[T1]")
                .getTester()
                .hasOnlyOneCell()
                .containingFormattedValue("100");
    }
}
