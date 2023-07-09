package com.activeviam.apps.cfg.pivot;

import com.activeviam.apps.cfg.PluginConfig;
import com.activeviam.apps.constants.StoreAndFieldConstants;
import com.activeviam.builders.StartBuilding;
import com.qfs.server.cfg.IActivePivotManagerDescriptionConfig;
import com.qfs.server.cfg.IDatastoreSchemaDescriptionConfig;
import com.quartetfs.biz.pivot.definitions.IActivePivotInstanceDescription;
import com.quartetfs.biz.pivot.definitions.IActivePivotManagerDescription;
import com.quartetfs.biz.pivot.definitions.ISelectionDescription;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

@RequiredArgsConstructor
@Configuration
@DependsOn(PluginConfig.BEAN_NAME)
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
    public static final String TIMESTAMP_FORMATTER = "DATE[HH:mm:ss]";

    public static final String NATIVE_MEASURES = "Native Measures";

    private final IDatastoreSchemaDescriptionConfig datastoreSchemaDescConfig;
    private final CubeConfig cubeConfig;

    @Override
    public IActivePivotManagerDescription managerDescription() {
        return StartBuilding.managerDescription(MANAGER_NAME)
                .withCatalog(CATALOG_NAME)
                .containingAllCubes()
                .withSchema(SCHEMA_NAME)
                .withSelection(createSchemaSelectionDescription())
                .withCube(createCubeDescription())
                .build();
    }

    /**
     * Creates the {@link ISelectionDescription} for Pivot Schema.
     *
     * @return The created selection description
     */
    ISelectionDescription createSchemaSelectionDescription() {
        return StartBuilding.selection(datastoreSchemaDescConfig.datastoreSchemaDescription())
                .fromBaseStore(StoreAndFieldConstants.TRADES_STORE_NAME)
                .withAllFields()
                .build();
    }

    IActivePivotInstanceDescription createCubeDescription() {
        return cubeConfig.configureCubeBuilder(StartBuilding.cube(CUBE_NAME)).build();
    }
}
