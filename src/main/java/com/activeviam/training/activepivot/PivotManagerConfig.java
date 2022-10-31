package com.activeviam.training.activepivot;

import com.activeviam.builders.StartBuilding;
import com.activeviam.desc.build.IActivePivotManagerDescriptionBuilder;
import com.qfs.desc.IDatastoreSchemaDescription;
import com.qfs.server.cfg.IActivePivotManagerDescriptionConfig;
import com.quartetfs.biz.pivot.definitions.IActivePivotInstanceDescription;
import com.quartetfs.biz.pivot.definitions.IActivePivotManagerDescription;
import com.quartetfs.biz.pivot.definitions.ISelectionDescription;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;

import java.util.List;

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


    private final IDatastoreSchemaDescription datastoreSchemaDescription;
    private final ISelectionDescription selectionDescription;
    private final List<IActivePivotInstanceDescription> cubeDescriptions;

    public PivotManagerConfig(
            @Qualifier(value = "pivot_schema_description") IDatastoreSchemaDescription datastoreSchemaDescription,
            ISelectionDescription selectionDescription,
            List<IActivePivotInstanceDescription> cubeDescriptions
    ) {
        this.selectionDescription = selectionDescription;
        this.cubeDescriptions = cubeDescriptions;
        this.datastoreSchemaDescription = datastoreSchemaDescription;
    }

    @Override
    public IActivePivotManagerDescription userManagerDescription() {
        IActivePivotManagerDescriptionBuilder.HasSelection managerDescription = StartBuilding.managerDescription(MANAGER_NAME)
                .withCatalog(CATALOG_NAME)
                .containingAllCubes()
                .withSchema(SCHEMA_NAME)
                .withSelection(selectionDescription);
        IActivePivotManagerDescriptionBuilder.BuildableActivePivotSchemaDescriptionBuilder managerWithCube = null;
        for (IActivePivotInstanceDescription cubeDescription : cubeDescriptions) {
            managerWithCube = managerDescription.withCube(cubeDescription);
        }
        if (managerWithCube == null) {
            return null;
        }
        return managerWithCube.build();
    }

    @Override
    public IDatastoreSchemaDescription userSchemaDescription() {
        return datastoreSchemaDescription;
    }

//    @Bean
//    public IActivePivotSchemaDescription activePivotSchemaDescription() {
//        return StartBuilding.cube().withName("").withCalculations(a -> a).withDimensions(builder -> builder.withDimension("").withSingleLevelHierarchy("")
//                .build();
//    }

}
