package com.activeviam.training.activepivot.schema;

import com.activeviam.builders.StartBuilding;
import com.qfs.desc.IDatastoreSchemaDescription;
import com.qfs.desc.IReferenceDescription;
import com.qfs.desc.IStoreDescription;
import com.qfs.desc.impl.DatastoreSchemaDescription;
import com.quartetfs.biz.pivot.definitions.ISelectionDescription;
import com.activeviam.training.constants.StoreAndFieldConstants;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SchemaConfig {

    /**
     * Creats the {@link IDatastoreSchemaDescription}
     *
     * @param stores     the stores in the datastore schema
     * @param references the references between the stores
     * @return the datastore schema description
     */
    @Bean("pivot_schema_description")
    public IDatastoreSchemaDescription datastoreSchemaDescription(
            List<IStoreDescription> stores,
            List<IReferenceDescription> references) {
        return new DatastoreSchemaDescription(stores, references);
    }

    /**
     * Creates the {@link ISelectionDescription} for Pivot Schema.
     *
     * @param datastoreDescription : The datastore description
     * @return The created selection description
     */
    @Bean
    public ISelectionDescription createSchemaSelectionDescription(
            @Qualifier("pivot_schema_description") final IDatastoreSchemaDescription datastoreDescription) {
        return StartBuilding.selection(datastoreDescription)
                .fromBaseStore(StoreAndFieldConstants.TRADES_STORE_NAME)
                .withAllFields()
                .usingReference(StoreAndFieldConstants.TRADES_TO_INSTRUMENT)
                .withAllFields()
                .except(StoreAndFieldConstants.INSTRUMENT_ID)
                .build();
    }

}
