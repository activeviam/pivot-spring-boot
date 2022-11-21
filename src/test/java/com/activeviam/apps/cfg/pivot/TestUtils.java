/*
 * (C) ActiveViam 2022
 * ALL RIGHTS RESERVED. This material is the CONFIDENTIAL and PROPRIETARY
 * property of ActiveViam. Any unauthorized use,
 * reproduction or transfer of this material is strictly prohibited
 */
package com.activeviam.apps.cfg.pivot;

import com.activeviam.copper.builders.ITransactionsBuilder;
import com.activeviam.copper.builders.impl.SimpleTransactionBuilder;

import java.time.LocalDate;

import static com.activeviam.apps.activepivot.data.datastore.StoreAndFieldConstants.TRADES_DETAILS_STORE_NAME;
import static com.activeviam.apps.activepivot.data.datastore.StoreAndFieldConstants.TRADES_STORE_NAME;

/**
 * @author ActiveViam
 */
public class TestUtils {

	public static ITransactionsBuilder createTestData() {
		return SimpleTransactionBuilder.start()
				.inStore(TRADES_STORE_NAME)
				.add(LocalDate.parse("2019-03-13"), "T1", 100d)
				.add(LocalDate.parse("2019-03-13"), "T2", 350d)
				.add(LocalDate.parse("2019-03-13"), "T3", 300d)
				.inStore(TRADES_DETAILS_STORE_NAME)
				.add("T1","AMZN")
				.add("T2","AAPL")
				.add("T3","AAPL")
				.end();

	}

}
