package com.activeviam.apps.cfg.pivot;

import com.activeviam.apps.cfg.DatastoreDescriptionConfig;
import com.activeviam.apps.pp.MultiplyAtLeafPostProcessor;
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
import com.quartetfs.biz.pivot.query.impl.QueryMonitoring;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import static com.activeviam.apps.constants.StoreAndFieldConstants.INSTRUMENTS_STORE_NAME;
import static com.activeviam.apps.constants.StoreAndFieldConstants.POSITIONS_STORE_NAME;

public class MeasuresTest {

    @BeforeClass
    public static void setup() {
        CopperRegistrations.setupRegistryForTests(MultiplyAtLeafPostProcessor.class);
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
                .inStore(POSITIONS_STORE_NAME)
                .add(1, 1, "A", "2B", "3BC", 45.35)
                .add(2, 1, "A", "2B", "3BD", 79.81)
                .add(3, 1, "A", "2C", "3AC", 42.42)
                .add(4, 2, "A", "2C", "3BC", 99.19)
                .add(5, 2, "A", "2C", "3CC", 25.83)

                .inStore(INSTRUMENTS_STORE_NAME)
                .add(1, 100d)
                .add(2, 50d)

                .end();
    }

    @Test
    public void runM1() {
        final CubeTester tester = builder.build(Measures::build);
        tester.mdxQuery("SELECT\n" +
                "  NON EMPTY {\n" +
                "    [Measures].[M1]\n" +
                "  } ON COLUMNS,\n" +
                "  NON EMPTY [Portfolio].[Portfolio].Members ON ROWS\n" +
                "  FROM [Cube]", new QueryMonitoring().enableExecutionTimingPrint().enableQueryPlanSummary())
                .getTester()
                .printCellSet();
    }

    @Test
    public void runM2() {
        final CubeTester tester = builder.build(Measures::build);
        tester.mdxQuery("SELECT\n" +
                "  NON EMPTY {\n" +
                "    [Measures].[M2]\n" +
                "  } ON COLUMNS,\n" +
                "  NON EMPTY [Portfolio].[Portfolio].Members ON ROWS\n" +
                "  FROM [Cube]", new QueryMonitoring().enableExecutionTimingPrint().enableQueryPlanSummary())
                .getTester()
                .printCellSet();
    }

    @Test
    public void runM3() {
        final CubeTester tester = builder.build(Measures::build);
        tester.mdxQuery("SELECT\n" +
                "  NON EMPTY {\n" +
                "    [Measures].[M3]\n" +
                "  } ON COLUMNS,\n" +
                "  NON EMPTY [Portfolio].[Portfolio].Members ON ROWS\n" +
                "  FROM [Cube]", new QueryMonitoring().enableExecutionTimingPrint().enableQueryPlanSummary())
                .getTester()
                .printCellSet();
    }

    @Test
    public void runM4() {
        final CubeTester tester = builder.build(Measures::build);
        tester.mdxQuery("SELECT\n" +
                "  NON EMPTY {\n" +
                "    [Measures].[M4]\n" +
                "  } ON COLUMNS,\n" +
                "  NON EMPTY [Portfolio].[Portfolio].Members ON ROWS\n" +
                "  FROM [Cube]", new QueryMonitoring().enableExecutionTimingPrint().enableQueryPlanSummary())
                .getTester()
                .printCellSet();
    }
}
