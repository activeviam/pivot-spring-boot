package com.activeviam.apps.cfg;

import com.activeviam.apps.constants.StoreAndFieldConstants;
import com.activeviam.tools.datastore.impl.AConfigurableSchema;

import static com.qfs.literal.ILiteralType.DOUBLE;
import static com.qfs.literal.ILiteralType.LOCAL_DATE;
import static com.qfs.literal.ILiteralType.STRING;

public class DefaultSchema extends AConfigurableSchema {

    public static final String SCHEMA = "Default";

    /**
     * {@inheritDoc}
     * If this method is not overridden in concrete implementation, it will not have any effect.
     */
    @Override
    public void createStores() {
        getConfigurator().addStore(SCHEMA,
                getConfigurator()
                        .storeBuilder(SCHEMA)
                        .withStoreName(StoreAndFieldConstants.TRADES_STORE_NAME)
                        .withField(StoreAndFieldConstants.ASOFDATE, LOCAL_DATE).asKeyField()
                        .withField(StoreAndFieldConstants.TRADES__TRADEID, STRING).asKeyField()
                        .withField(StoreAndFieldConstants.TRADES__NOTIONAL, DOUBLE)
                        .build()
        );
    }
}
