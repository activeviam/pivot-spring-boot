package com.activeviam.apps.activepivot.pivot;

import com.activeviam.apps.activepivot.configurers.IDatastoreConfigurer;
import com.activeviam.apps.activepivot.configurers.ISchemaSelectionConfigurer;
import com.activeviam.builders.StartBuilding;
import com.activeviam.desc.build.IActivePivotManagerDescriptionBuilder;
import com.activeviam.desc.build.ICanBuildActivePivotManagerDescription;
import com.activeviam.desc.build.ICanStartBuildingSchema;
import com.activeviam.fwk.ActiveViamRuntimeException;
import com.qfs.desc.IDatastoreSchemaDescription;
import com.qfs.server.cfg.IActivePivotManagerDescriptionConfig;
import com.quartetfs.biz.pivot.definitions.IActivePivotManagerDescription;
import org.springframework.context.annotation.Configuration;

import java.util.List;

import static com.activeviam.apps.activepivot.pivot.CubeConstants.CATALOG_NAME;
import static com.activeviam.apps.activepivot.pivot.CubeConstants.MANAGER_NAME;

@Configuration
public class ActivePivotManagerConfiguration implements IActivePivotManagerDescriptionConfig {

    private final List<ISchemaSelectionConfigurer> schemaConfigurers;
    private final IDatastoreConfigurer datastoreConfigurer;

    public ActivePivotManagerConfiguration(
            List<ISchemaSelectionConfigurer> schemaConfigurers, IDatastoreConfigurer datastoreConfigurer) {
        this.schemaConfigurers = schemaConfigurers;
        this.datastoreConfigurer = datastoreConfigurer;
    }

    @Override
    public IActivePivotManagerDescription userManagerDescription() {
        var builder = StartBuilding.managerDescription(MANAGER_NAME)
                .withCatalog(CATALOG_NAME)
                .containingAllCubes();

        if (schemaConfigurers.isEmpty()) {
            throw new ActiveViamRuntimeException("No SelectionSchemaConfigurer has been defined");
        }

        ICanStartBuildingSchema schemaBuilder = builder;
        for (var schemaConfigurer : schemaConfigurers) {
            IActivePivotManagerDescriptionBuilder.HasSelection schema = schemaBuilder
                    .withSchema(schemaConfigurer.schemaName())
                    .withSelection(schemaConfigurer.createSchemaSelectionDescription(userSchemaDescription()));

            for (var cubeConfigurer : schemaConfigurer.cubes()) {
                schema = (IActivePivotManagerDescriptionBuilder.HasSelection)
                        schema.withCube(cubeConfigurer.cubeDescription());
            }
            schemaBuilder = (ICanStartBuildingSchema) schema;
        }
        return ((ICanBuildActivePivotManagerDescription) schemaBuilder).build();
    }

    @Override
    public IDatastoreSchemaDescription userSchemaDescription() {
        return datastoreConfigurer.datastoreSchemaDescription();
    }
}
