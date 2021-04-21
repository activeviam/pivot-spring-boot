package com.activeviam.apps.cfg;

import com.activeviam.apps.constants.StoreAndFieldConstants;
import com.qfs.desc.IDatastoreSchemaDescription;
import com.qfs.desc.IReferenceDescription;
import com.qfs.desc.IStoreDescription;
import com.qfs.desc.impl.DatastoreSchemaDescription;
import com.qfs.desc.impl.ReferenceDescription;
import com.qfs.desc.impl.StoreDescriptionBuilder;
import org.springframework.context.annotation.Configuration;

import java.util.Collection;
import java.util.LinkedList;

import static com.qfs.literal.ILiteralType.*;

@Configuration
public class DatastoreDescriptionConfig {

    public static IStoreDescription createTradesStoreDescription() {
        return new StoreDescriptionBuilder().withStoreName(StoreAndFieldConstants.TRADES_STORE_NAME)
                .withField(StoreAndFieldConstants.TRADES_TRADEID, STRING).asKeyField()
                .withField(StoreAndFieldConstants.TRADES_NOTIONAL, DOUBLE)
                .withField(StoreAndFieldConstants.TRADES_PRODUCT, STRING)
                .withField(StoreAndFieldConstants.NETTINGSETID, STRING)
                .withField(StoreAndFieldConstants.BOOKID, STRING)
                .withField(StoreAndFieldConstants.TRADES_DIRECTION, STRING)
                .withField(StoreAndFieldConstants.TRADES_INPUTCURRENCY, STRING)
                .withField(StoreAndFieldConstants.TRADES_INSTRUMENT, STRING)
                .withField(StoreAndFieldConstants.TRADES_ASSETCLASS, STRING)
                .withNullableField(StoreAndFieldConstants.TRADES_SUBCLASS, STRING)
                .withNullableField(StoreAndFieldConstants.TRADES_OPTIONTYPE, STRING)
                .withField(StoreAndFieldConstants.TRADES_UNDERLYING, STRING)
                .withField(StoreAndFieldConstants.TRADES_MATURITY, LOCAL_DATE)
                .withField(StoreAndFieldConstants.TRADES_NOTIONAL, DOUBLE)
                .withField(StoreAndFieldConstants.TRADES_MARKETVALUE, DOUBLE)
                .build();
    }

    public static IStoreDescription createBooksStoreDescription() {
        return new StoreDescriptionBuilder().withStoreName(StoreAndFieldConstants.BOOKS_STORE_NAME)
                .withField(StoreAndFieldConstants.BOOKID, STRING).asKeyField()
                .withField(StoreAndFieldConstants.BOOKS_COMPANY, STRING)
                .withField(StoreAndFieldConstants.BOOKS_DESK, STRING)
                .build();
    }

    public static IStoreDescription createFxRatesStoreDescription() {
        return new StoreDescriptionBuilder().withStoreName(StoreAndFieldConstants.FX_RATES_STORE_NAME)
                .withField(StoreAndFieldConstants.FX_RATES_BASECCY, STRING).asKeyField()
                .withField(StoreAndFieldConstants.FX_RATES_COUNTERCCY, STRING)
                .withField(StoreAndFieldConstants.FX_RATES_FXRATE, DOUBLE)
                .build();
    }

    public static IStoreDescription createNettingSetsStoreDescription() {
        return new StoreDescriptionBuilder().withStoreName(StoreAndFieldConstants.NETTINGSETS_STORE_NAME)
                .withField(StoreAndFieldConstants.NETTINGSETID, STRING).asKeyField()
                .withField(StoreAndFieldConstants.NETTINGSETS_NETTINGNAME, STRING)
                .withField(StoreAndFieldConstants.NETTINGSETS_NETTINGTYPE, STRING)
                .withField(StoreAndFieldConstants.COUNTERPARTYID, STRING)
                .withField(StoreAndFieldConstants.NETTINGSETS_COLLATERAL, DOUBLE)
                .withField(StoreAndFieldConstants.NETTINGSETS_MTA, DOUBLE)
                .withField(StoreAndFieldConstants.NETTINGSETS_INPUTCURRENCY, STRING)
                .build();
    }

    public static IStoreDescription createCounterpartiesStoreDescription() {
        return new StoreDescriptionBuilder().withStoreName(StoreAndFieldConstants.COUNTERPARTIES_STORE_NAME)
                .withField(StoreAndFieldConstants.COUNTERPARTYID, STRING).asKeyField()
                .withField(StoreAndFieldConstants.COUNTERPARTIES_COUNTERPARTYNAME, STRING)
                .withField(StoreAndFieldConstants.COUNTERPARTIES_RATING, STRING)
                .withField(StoreAndFieldConstants.COUNTERPARTIES_SECTOR, STRING)
                .withField(StoreAndFieldConstants.COUNTERPARTIES_COUNTRYOFRISK, STRING)
                .build();
    }

    public static Collection<IReferenceDescription> references() {
        final Collection<IReferenceDescription> references = new LinkedList<>();

        references.add(ReferenceDescription.builder()
                .fromStore(StoreAndFieldConstants.TRADES_STORE_NAME)
                .toStore(StoreAndFieldConstants.NETTINGSETS_STORE_NAME)
                .withName(StoreAndFieldConstants.TRADES_TO_NETTINGSETS)
                .withMapping(StoreAndFieldConstants.NETTINGSETID, StoreAndFieldConstants.NETTINGSETID)
                .dontIndexOwner()
                .build());

        references.add(ReferenceDescription.builder()
                .fromStore(StoreAndFieldConstants.TRADES_STORE_NAME)
                .toStore(StoreAndFieldConstants.BOOKS_STORE_NAME)
                .withName(StoreAndFieldConstants.TRADES_TO_BOOKS)
                .withMapping(StoreAndFieldConstants.BOOKID, StoreAndFieldConstants.BOOKID)
                .dontIndexOwner()
                .build());

        references.add(ReferenceDescription.builder()
                .fromStore(StoreAndFieldConstants.NETTINGSETS_STORE_NAME)
                .toStore(StoreAndFieldConstants.COUNTERPARTIES_STORE_NAME)
                .withName(StoreAndFieldConstants.NETTINGSETS_TO_COUNTERPARTIES)
                .withMapping(StoreAndFieldConstants.COUNTERPARTYID, StoreAndFieldConstants.COUNTERPARTYID)
                .dontIndexOwner()
                .build());

        return references;
    }

    /**
     * Provide the schema description of the datastore.
     * <p>
     * It is based on the descriptions of the stores in the datastore, the descriptions of the references
     * between those stores, and the optimizations and constraints set on the schema.
     *
     * @return schema description
     */
    public static IDatastoreSchemaDescription schemaDescription() {

        final Collection<IStoreDescription> stores = new LinkedList<>();
        stores.add(createTradesStoreDescription());
        stores.add(createBooksStoreDescription());
        stores.add(createCounterpartiesStoreDescription());
        stores.add(createFxRatesStoreDescription());
        stores.add(createNettingSetsStoreDescription());

        return new DatastoreSchemaDescription(stores, references());
    }
}
