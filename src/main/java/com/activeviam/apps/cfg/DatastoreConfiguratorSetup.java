package com.activeviam.apps.cfg;

import com.activeviam.tools.datastore.IConfigurableSchema;
import com.activeviam.tools.datastore.IDatastoreConfigurator;
import com.activeviam.tools.datastore.IDatastoreConfiguratorSetup;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DatastoreConfiguratorSetup implements IDatastoreConfiguratorSetup {
    /**
     * Adds all customisations to the configurator.
     *
     * @param configurator The application datastore configurator.
     */
    @Override
    public void addModifications(IDatastoreConfigurator configurator) {

    }

    /**
     * Builds the datastores and references included in schemas. Must be called after {@link #addModifications}.
     *
     * @param configurator The application datastore configurator.
     */
    @Override
    public void buildSchemas(IDatastoreConfigurator configurator) {
        IConfigurableSchema schema = new DefaultSchema();
        schema.setConfigurator(configurator);
        schema.createStores();
        schema.createReferences();
        configurator.enableSchema(DefaultSchema.SCHEMA);
    }
}
