package com.activeviam.apps.constants;

public class StoreAndFieldConstants {

	/*********************** Cube name **********************/
	public static final String CUBE_NAME = "Zoby";

	/*********************** Stores names **********************/
	public static final String TRADES_STORE_NAME = "Trades";
	public static final String PRODUCT_STORE_NAME = "Products";
	public static final String TRADERS_STORE_NAME = "Traders";
	public static final String FOREX_STORE_NAME = "Forex";

	/********************* Stores fields ***********************/
	public static final String ASOFDATE = "AsOfDate";
	public static final String TRADES__TRADEID = "TradeID";
	public static final String TRADES__NOTIONAL = "Notional";
	public static final String TRADES__TRADER = "Trader";
	public static final String TRADES__PRODUCT = "Product";
	public static final String TRADES__CURRENCY = "Currency";

	public static final String PRODUCT__PRODUCTID = "ProductID";
	public static final String PRODUCT__VALUE = "Value";

	public static final String TRADERS__TRADERID = "TraderID";
	public static final String TRADERS__NAME = "Name";
	public static final String TRADERS__DESK = "Desk";

	public static final String FOREX_CURRENCY = "ForexCurrency";
	public static final String FOREX_TARGET_CURRENCY = "ForexTargetCurrency";
	public static final String FOREX_RATE = "Rate";

	/********************* Join names ***********************/
	public static final String TRADES_TO_TRADER = "TradesToTrader";
	public static final String TRADES_TO_PRODUCT = "TradesToProduct";

}
