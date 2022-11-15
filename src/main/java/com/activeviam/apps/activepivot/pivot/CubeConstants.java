/*
 * (C) ActiveViam 2022
 * ALL RIGHTS RESERVED. This material is the CONFIDENTIAL and PROPRIETARY
 * property of ActiveViam. Any unauthorized use,
 * reproduction or transfer of this material is strictly prohibited
 */
package com.activeviam.apps.activepivot.pivot;

import com.activeviam.apps.activepivot.data.datastore.StoreAndFieldConstants;

/**
 * @author ActiveViam
 */
public class CubeConstants {

    private CubeConstants() {}
    /* *********************/
    /* OLAP Property names */
    /* *********************/

    public static final String MANAGER_NAME = "Manager";
    public static final String CATALOG_NAME = "Catalog";
    public static final String TRADES_SCHEMA_NAME = "TradesSchema";
    public static final String TRADES_CUBE_NAME = "TradesCube";

    public static final String TICKERS_SCHEMA_NAME = "TickersSchema";
    public static final String TICKERS_CUBE_NAME = "TickersCube";

    /* ********** */
    /* Formatters */
    /* ********** */
    public static final String DOUBLE_FORMATTER = "DOUBLE[#,###.##]";
    public static final String INT_FORMATTER = "INT[#,###]";
    public static final String NATIVE_MEASURES = "Native Measures";

    /* *********************/
    /* Dimension names     */
    /* *********************/

    public static final String TRADE_ID = StoreAndFieldConstants.TRADES_TRADEID;
    public static final String AS_OF_DATE = StoreAndFieldConstants.ASOFDATE;

    public static final String TRADE_TICKER = StoreAndFieldConstants.TRADES_TICKER;
}
