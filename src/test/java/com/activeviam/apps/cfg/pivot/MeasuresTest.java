package com.activeviam.apps.cfg.pivot;

import com.activeviam.apps.cfg.DatastoreDescriptionConfig;
import com.activeviam.builders.StartBuilding;
import com.activeviam.copper.CopperRegistrations;
import com.activeviam.copper.builders.ITransactionsBuilder;
import com.activeviam.copper.builders.impl.SimpleTransactionBuilder;
import com.activeviam.copper.testing.CubeTester;
import com.activeviam.copper.testing.CubeTesterBuilder;
import com.activeviam.copper.testing.CubeTesterBuilderExtension;
import com.qfs.desc.IDatastoreSchemaDescription;
import com.quartetfs.biz.pivot.definitions.IActivePivotInstanceDescription;
import com.quartetfs.biz.pivot.definitions.ISelectionDescription;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.time.LocalDate;

import static com.activeviam.apps.constants.StoreAndFieldConstants.TRADES_STORE_NAME;
import static com.activeviam.apps.constants.StoreAndFieldConstants.TRADES__NOTIONAL;

class MeasuresTest {

    @BeforeAll
    public static void setup() {
        CopperRegistrations.setupRegistryForTests();
    }

    @RegisterExtension
    public CubeTesterBuilderExtension builder = new CubeTesterBuilderExtension(createTester());

    /**
     * Creates the tester using the descriptions of the project.
     *
     * @return The tester.
     */
    public static CubeTesterBuilder createTester() {
        IDatastoreSchemaDescription datastoreDescription =
                DatastoreDescriptionConfig.schemaDescription();
        ISelectionDescription selectionDescription = PivotManagerConfig.createSchemaSelectionDescription(datastoreDescription);
        IActivePivotInstanceDescription cubeDescription = StartBuilding.cube()
                .withName("Cube")
                .withDimensions(CubeConfig::dimensions)
                .build();
        return new CubeTesterBuilder(
                datastoreDescription,
                selectionDescription,
                createTestData(),
                cubeDescription);
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

        final CubeTester tester = builder.build(Measures::build);
        tester.query()
                .forMeasures(TRADES__NOTIONAL)
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
        final CubeTester tester = builder.build(Measures::build);
        tester.mdxQuery( "SELECT" +
                "  [Measures].[Notional] ON COLUMNS" +
                "  FROM [Cube]" +
                "  WHERE [TradeID].[TradeID].[ALL].[AllMember].[T1]")
                .getTester()
                .hasOnlyOneCell()
                .containingFormattedValue("100");
    }
}
