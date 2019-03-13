package com.activeviam.apps.pivotspringboot.activepivot;

import com.activeviam.builders.StartBuilding;
import com.activeviam.copper.Registrations;
import com.activeviam.copper.foundry.impl.SimpleQueryableCubeFoundry;
import com.qfs.desc.IDatastoreSchemaDescription;
import com.qfs.store.IDatastore;
import com.quartetfs.biz.pivot.IActivePivotManager;
import com.quartetfs.biz.pivot.definitions.IActivePivotInstanceDescription;
import com.quartetfs.biz.pivot.definitions.IActivePivotManagerDescription;
import com.quartetfs.biz.pivot.definitions.impl.ActivePivotDatastorePostProcessor;
import com.quartetfs.fwk.AgentException;
import com.quartetfs.fwk.Registry;
import com.quartetfs.fwk.contributions.impl.ClasspathContributionProvider;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;

import static com.activeviam.apps.pivotspringboot.activepivot.DatastoreDescriptionConfig.createTradesStoreDescription;
import static com.activeviam.apps.pivotspringboot.activepivot.PivotManager.*;
import static com.activeviam.apps.pivotspringboot.activepivot.StoreAndFieldConstants.TRADES_STORE_NAME;

public class MeasuresTest {

    IActivePivotManager manager;

    @AfterClass
    public static void afterAll() {
        Registrations.registrySetupWithClasses.clear();
    }

    @Before
    public void cubeSetup() throws AgentException {
        System.setProperty("activeviam.testPhase", "true");
        // Set the registry
        Registry.setContributionProvider(new ClasspathContributionProvider("com.qfs", "com.quartetfs", "com.activeviam"));

        // Build a datastore
        IDatastoreSchemaDescription datastoreSchemaDescription = com.activeviam.builders.StartBuilding.datastoreSchema()
                .withStore(createTradesStoreDescription())
                .build();

        IActivePivotManagerDescription managerDesc = com.activeviam.builders.StartBuilding.managerDescription().withSchema()
                .withSelection(createSchemaSelectionDescription(datastoreSchemaDescription))
                .withCube(createTestCubeDescription())
                .build();

        final IDatastore datastore = com.activeviam.builders.StartBuilding.datastore()
                .setSchemaDescription(datastoreSchemaDescription)
                .addSchemaDescriptionPostProcessors(ActivePivotDatastorePostProcessor.createFrom(managerDesc))
                .build();

        // Build a start the manager
        manager = StartBuilding.manager().setDatastoreAndDescription(datastore, datastoreSchemaDescription)
                .setDescription(managerDesc)
                .buildAndStart();

        datastore.edit(d -> {
            d.add(TRADES_STORE_NAME, LocalDate.parse("2019-03-13"), "T1", 100d);
            d.add(TRADES_STORE_NAME, LocalDate.parse("2019-03-13"), "T2", 350d);
            d.add(TRADES_STORE_NAME, LocalDate.parse("2019-03-13"), "T3", 300d);
        });
    }

    /**
     * Here we build our test cube instance. Let's use the one defined in the main project (ie the whole cube)
     * A smaller test could use a subset of the dimensions if we wished
     */
    public static IActivePivotInstanceDescription createTestCubeDescription() {
        return configureCubeBuilder(StartBuilding.cube(CUBE_NAME)).build();
    }

    /**
     * Here is the actual test. Check that the numbers sum up correctly
     */
    @Test
    public void testSimpleSum() {
        SimpleQueryableCubeFoundry.executeMdxQuery(manager, "SELECT" +
                "  [Measures].[Notional] ON COLUMNS" +
                "  FROM [Cube]")
                .getTester()
                .hasOnlyOneCell()
                .containingFormattedValue("750");
    }

    /**
     * Here is the actual test. Check that the numbers sum up correctly
     */
    @Test
    public void testSimpleSum_withSlicer() {
        SimpleQueryableCubeFoundry.executeMdxQuery(manager, "SELECT" +
                "  [Measures].[Notional] ON COLUMNS" +
                "  FROM [Cube]" +
                "  WHERE [TradeID].[TradeId].[TradeId].[All].[AllMember].[T1]")
                .getTester()
                .hasOnlyOneCell()
                .containingFormattedValue("100");
    }
}
