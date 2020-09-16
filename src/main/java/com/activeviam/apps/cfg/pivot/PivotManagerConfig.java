package com.activeviam.apps.cfg.pivot;

import com.activeviam.apps.constants.StoreAndFieldConstants;
import com.activeviam.builders.StartBuilding;
import com.activeviam.tools.datastore.IDatastoreConfigurator;
import com.qfs.desc.IDatastoreSchemaDescription;
import com.qfs.server.cfg.IActivePivotManagerDescriptionConfig;
import com.quartetfs.biz.pivot.definitions.IActivePivotInstanceDescription;
import com.quartetfs.biz.pivot.definitions.IActivePivotManagerDescription;
import com.quartetfs.biz.pivot.definitions.ISelectionDescription;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PivotManagerConfig implements IActivePivotManagerDescriptionConfig {

    /* *********************/
    /* OLAP Property names */
    /* *********************/

    public static final String MANAGER_NAME = "Manager";
    public static final String CATALOG_NAME = "Catalog";
    public static final String SCHEMA_NAME = "Schema";
    public static final String CUBE_NAME = "Cube";

    /* ********** */
    /* Formatters */
    /* ********** */
    public static final String DOUBLE_FORMATTER = "DOUBLE[#,###.##]";
    public static final String INT_FORMATTER = "INT[#,###]";
    public static final String NATIVE_MEASURES = "Native Measures";

    @Autowired
    protected IDatastoreConfigurator configurator;

    @Override
    public IActivePivotManagerDescription userManagerDescription() {
        return StartBuilding.managerDescription(MANAGER_NAME)
                .withCatalog(CATALOG_NAME)
                .containingAllCubes()
                .withSchema(SCHEMA_NAME)
                .withSelection(createSchemaSelectionDescription(userSchemaDescription()))
                .withCube(createCubeDescription())
                .build();
    }

    @Override
    public IDatastoreSchemaDescription userSchemaDescription() {
        return configurator.buildSchemaDescription();
    }

    /**
     * Creates the {@link ISelectionDescription} for Pivot Schema.
     *
     * @param datastoreDescription : The datastore description
     * @return The created selection description
     */
    public static ISelectionDescription createSchemaSelectionDescription(
            final IDatastoreSchemaDescription datastoreDescription) {
        return StartBuilding.selection(datastoreDescription)
                .fromBaseStore(StoreAndFieldConstants.TRADES_STORE_NAME)
                .withAllFields()
                .build();
    }

    public static IActivePivotInstanceDescription createCubeDescription() {
        return CubeConfig.configureCubeBuilder(StartBuilding.cube(CUBE_NAME)).build();
    }
}
