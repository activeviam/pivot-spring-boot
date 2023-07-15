package com.activeviam.apps.cfg;

import com.activeviam.apps.constants.StoreAndFieldConstants;
import com.activeviam.builders.StartBuilding;
import com.quartetfs.biz.pivot.definitions.ISelectionDescription;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class DatastoreSelectionConfig {

    private final DatastoreSchemaConfig datastoreSchemaConfig;

    /**
     * Creates the {@link ISelectionDescription} for Pivot Schema.
     *
     * @return The created selection description
     */
    public ISelectionDescription createSchemaSelectionDescription() {
        return StartBuilding.selection(datastoreSchemaConfig.datastoreSchemaDescription())
                .fromBaseStore(StoreAndFieldConstants.TRADES_STORE_NAME)
                .withAllFields()
                .build();
    }
}
