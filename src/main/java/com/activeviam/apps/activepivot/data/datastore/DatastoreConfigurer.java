package com.activeviam.apps.activepivot.data.datastore;

import static com.qfs.literal.ILiteralType.DOUBLE;
import static com.qfs.literal.ILiteralType.LOCAL_DATE;
import static com.qfs.literal.ILiteralType.STRING;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

import com.activeviam.apps.activepivot.configurers.IDatastoreConfigurer;
import com.qfs.desc.IDatastoreSchemaDescription;
import com.qfs.desc.IReferenceDescription;
import com.qfs.desc.IStoreDescription;
import com.qfs.desc.impl.DatastoreSchemaDescription;
import com.qfs.desc.impl.StoreDescriptionBuilder;
import org.springframework.stereotype.Component;

@Component
public class DatastoreConfigurer implements IDatastoreConfigurer {

	private static IStoreDescription createTradesStoreDescription() {
		return new StoreDescriptionBuilder().withStoreName(StoreAndFieldConstants.TRADES_STORE_NAME)
				.withField(StoreAndFieldConstants.ASOFDATE, LOCAL_DATE).asKeyField()
				.withField(StoreAndFieldConstants.TRADES_TRADEID, STRING).asKeyField()
				.withField(StoreAndFieldConstants.TRADES_NOTIONAL, DOUBLE)
				.build();
	}

	private static Collection<IReferenceDescription> references() {
		return Collections.emptyList();
	}

	/**
	 *
	 * Provide the schema description of the datastore.
	 * <p>
	 * It is based on the descriptions of the stores in the datastore, the descriptions of the references between those stores, and the optimizations and
	 * constraints set on the schema.
	 *
	 * @return schema description
	 */
	public IDatastoreSchemaDescription schemaDescription() {

		final Collection<IStoreDescription> stores = new LinkedList<>();
		stores.add(createTradesStoreDescription());

		return new DatastoreSchemaDescription(stores, references());
	}
}
