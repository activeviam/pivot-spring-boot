package com.activeviam.apps.cfg;

import static com.qfs.literal.ILiteralType.DOUBLE;
import static com.qfs.literal.ILiteralType.LOCAL_DATE;
import static com.qfs.literal.ILiteralType.STRING;
import com.activeviam.apps.constants.StoreAndFieldConstants;
import com.qfs.desc.IDatastoreSchemaDescription;
import com.qfs.desc.IReferenceDescription;
import com.qfs.desc.IStoreDescription;
import com.qfs.desc.impl.DatastoreSchemaDescription;
import com.qfs.desc.impl.StoreDescriptionBuilder;
import java.util.Collection;
import java.util.LinkedList;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DatastoreDescriptionConfig {

	public static IStoreDescription createTradesStoreDescription() {
		return new StoreDescriptionBuilder().withStoreName(StoreAndFieldConstants.TRADES_STORE_NAME)
				.withField(StoreAndFieldConstants.ASOFDATE, LOCAL_DATE).asKeyField()
				.withField(StoreAndFieldConstants.TRADES__TRADEID, STRING).asKeyField()
				.withField(StoreAndFieldConstants.TRADES__NOTIONAL, DOUBLE)
				.build();
	}

	public static Collection<IReferenceDescription> references() {
		final Collection<IReferenceDescription> references = new LinkedList<>();
		return references;
	}

	/**
	 * Provides the schema description of the datastore.
	 *
	 * <p>It is based on the descriptions of the stores in the datastore,
	 * the descriptions of the references between those stores,
	 * and the optimizations and constraints set on the schema.
	 *
	 * @return schema description
	 */
	public static IDatastoreSchemaDescription schemaDescription() {
		final Collection<IStoreDescription> stores = new LinkedList<>();
		stores.add(createTradesStoreDescription());

		return new DatastoreSchemaDescription(stores, references());
	}

}
