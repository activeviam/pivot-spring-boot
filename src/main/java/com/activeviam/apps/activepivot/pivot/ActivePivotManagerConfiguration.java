package com.activeviam.apps.activepivot.pivot;

import com.activeviam.apps.activepivot.configurers.IDatastoreConfigurer;
import com.activeviam.apps.activepivot.configurers.ICubeConfigurer;
import com.activeviam.apps.activepivot.configurers.ISchemaSelectionConfigurer;
import com.activeviam.builders.StartBuilding;
import com.qfs.desc.IDatastoreSchemaDescription;
import com.qfs.server.cfg.IActivePivotManagerDescriptionConfig;
import com.quartetfs.biz.pivot.definitions.IActivePivotManagerDescription;
import org.springframework.context.annotation.Configuration;

import static com.activeviam.apps.activepivot.pivot.CubeConstants.*;

@Configuration
public class ActivePivotManagerConfiguration implements IActivePivotManagerDescriptionConfig {

    private final ICubeConfigurer cubeConfigurer;
    private final ISchemaSelectionConfigurer schemaConfigurer;
    private final IDatastoreConfigurer datastoreConfigurer;

    public ActivePivotManagerConfiguration(ICubeConfigurer cubeConfigurer, ISchemaSelectionConfigurer schemaConfigurer, IDatastoreConfigurer datastoreConfigurer) {
        this.cubeConfigurer = cubeConfigurer;
        this.schemaConfigurer = schemaConfigurer;
        this.datastoreConfigurer = datastoreConfigurer;
    }

    @Override
    public IActivePivotManagerDescription userManagerDescription() {
        return StartBuilding.managerDescription(MANAGER_NAME)
                .withCatalog(CATALOG_NAME)
                .containingAllCubes()
                .withSchema(schemaConfigurer.schemaName())
                .withSelection(schemaConfigurer.createSchemaSelectionDescription(userSchemaDescription()))
                .withCube(cubeConfigurer.cubeDescription())
                .build();
    }


    @Override
    public IDatastoreSchemaDescription userSchemaDescription() {
        return datastoreConfigurer.schemaDescription();
    }

}
