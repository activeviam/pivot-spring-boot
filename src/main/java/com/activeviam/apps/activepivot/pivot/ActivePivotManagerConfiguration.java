package com.activeviam.apps.activepivot.pivot;

import com.activeviam.apps.activepivot.data.datastore.DatastoreConfigurer;
import com.activeviam.builders.StartBuilding;
import com.qfs.desc.IDatastoreSchemaDescription;
import com.qfs.server.cfg.IActivePivotManagerDescriptionConfig;
import com.quartetfs.biz.pivot.definitions.IActivePivotManagerDescription;
import org.springframework.context.annotation.Configuration;

import static com.activeviam.apps.activepivot.pivot.CubeConstants.*;

@Configuration
public class ActivePivotManagerConfiguration implements IActivePivotManagerDescriptionConfig {

    private final CubeConfigurer cubeConfigurer;
    private final SchemaSelectionConfigurer schemaConfigurer;
    private final DatastoreConfigurer datastoreConfigurer;

    public ActivePivotManagerConfiguration(
            CubeConfigurer cubeConfigurer,
            SchemaSelectionConfigurer schemaConfigurer,
            DatastoreConfigurer datastoreConfigurer) {
        this.cubeConfigurer = cubeConfigurer;
        this.schemaConfigurer = schemaConfigurer;
        this.datastoreConfigurer = datastoreConfigurer;
    }

    @Override
    public IActivePivotManagerDescription userManagerDescription() {
        var builder = StartBuilding.managerDescription(MANAGER_NAME)
                .withCatalog(CATALOG_NAME)
                .containingAllCubes();

        return builder.withSchema(schemaConfigurer.schemaName())
                .withSelection(schemaConfigurer.createSchemaSelectionDescription(userSchemaDescription()))
                .withCube(cubeConfigurer.cubeDescription())
                .build();
    }

    @Override
    public IDatastoreSchemaDescription userSchemaDescription() {
        return datastoreConfigurer.datastoreSchemaDescription();
    }
}
