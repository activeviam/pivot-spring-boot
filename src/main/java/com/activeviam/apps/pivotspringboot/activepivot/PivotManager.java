package com.activeviam.apps.pivotspringboot.activepivot;

import com.activeviam.builders.StartBuilding;
import com.activeviam.desc.build.ICanBuildCubeDescription;
import com.activeviam.desc.build.ICanStartBuildingMeasures;
import com.activeviam.desc.build.ICubeDescriptionBuilder;
import com.activeviam.desc.build.IHasAtLeastOneMeasure;
import com.activeviam.desc.build.dimensions.ICanStartBuildingDimensions;
import com.qfs.desc.IDatastoreSchemaDescription;
import com.qfs.server.cfg.IActivePivotManagerDescriptionConfig;
import com.quartetfs.biz.pivot.cube.dimension.IDimension;
import com.quartetfs.biz.pivot.cube.hierarchy.ILevelInfo;
import com.quartetfs.biz.pivot.definitions.IActivePivotInstanceDescription;
import com.quartetfs.biz.pivot.definitions.IActivePivotManagerDescription;
import com.quartetfs.biz.pivot.definitions.ISelectionDescription;
import com.quartetfs.fwk.ordering.impl.ReverseOrderComparator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PivotManager implements IActivePivotManagerDescriptionConfig {

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
    protected IDatastoreSchemaDescription datastoreDescription;

    @Override
    @Bean()
    public IActivePivotManagerDescription managerDescription() {

        return StartBuilding.managerDescription(MANAGER_NAME)
                .withCatalog(CATALOG_NAME)
                .containingAllCubes()
                .withSchema(SCHEMA_NAME)
                .withSelection(createSchemaSelectionDescription(this.datastoreDescription))
                .withCube(createCubeDescription())
                .build();
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
        return configureCubeBuilder(StartBuilding.cube(CUBE_NAME)).build();
    }

    /**
     * Configures the given builder in order to created the cube description.
     *
     * @param builder The builder to configure
     * @return The configured builder
     */
    public static ICanBuildCubeDescription<IActivePivotInstanceDescription> configureCubeBuilder(
            final ICubeDescriptionBuilder.INamedCubeDescriptionBuilder builder) {

        return builder
                .withContributorsCount()
                .withinFolder(NATIVE_MEASURES)
                .withAlias("Count")
                .withFormatter(INT_FORMATTER)

                .withMeasures(PivotManager::measures)
                .withDimensions(PivotManager::dimensions)
                // Aggregate provider
                .withAggregateProvider()
                .jit()

                // Shared context values
                // Query maximum execution time (before timeout cancellation): 1h
                .withSharedMdxContext().aggressiveFormulaEvaluation(true).end()
                .withSharedDrillthroughProperties()
                .withMaxRows(10000)
                .end()

                ;
    }

    /**
     * Adds the dimensions descriptions to the input
     * builder.
     *
     * @param builder The cube builder
     * @return The builder for chained calls
     */
    public static ICanBuildCubeDescription<IActivePivotInstanceDescription> dimensions(ICanStartBuildingDimensions builder) {

        return builder
                .withSingleLevelDimensions(
                        StoreAndFieldConstants.TRADES__TRADEID
                )
                .withDimension(StoreAndFieldConstants.ASOFDATE)
                .withType(IDimension.DimensionType.TIME)
                .withHierarchy(StoreAndFieldConstants.ASOFDATE).slicing()
                .withLevelOfSameName()
                .withType(ILevelInfo.LevelType.TIME).withComparator(ReverseOrderComparator.type)
                ;

    }

    public static IHasAtLeastOneMeasure measures(final ICanStartBuildingMeasures builder) {
        return builder
                // Actual measures
                .withAggregatedMeasure().sum(StoreAndFieldConstants.TRADES__NOTIONAL).withName(StoreAndFieldConstants.TRADES__NOTIONAL).withFormatter(DOUBLE_FORMATTER)
                ;
    }


}
