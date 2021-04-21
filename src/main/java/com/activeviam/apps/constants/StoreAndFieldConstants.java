package com.activeviam.apps.constants;

public class StoreAndFieldConstants {

    /*********************** Stores names **********************/
    public static final String TRADES_STORE_NAME = "Trades";
    public static final String BOOKS_STORE_NAME = "Books";
    public static final String COUNTERPARTIES_STORE_NAME = "Counterparties";
    public static final String FX_RATES_STORE_NAME = "FXRates";
    public static final String NETTINGSETS_STORE_NAME = "NettingSets";

    /********************* Stores fields ***********************/
    public static final String NETTINGSETID = "NettingSetId";
    public static final String BOOKID = "BookId";
    public static final String COUNTERPARTYID = "CounterpartyId";

    public static final String ASOFDATE = "AsOfDate";
    public static final String TRADES_TRADEID = "TradeID";
    public static final String TRADES_NOTIONAL = "Notional";
    public static final String TRADES_PRODUCT = "Product";
    public static final String TRADES_DIRECTION = "Direction";
    public static final String TRADES_INPUTCURRENCY = "InputCurrency";
    public static final String TRADES_INSTRUMENT = "Instrument";
    public static final String TRADES_ASSETCLASS = "AssetClass";
    public static final String TRADES_SUBCLASS = "SubClass";
    public static final String TRADES_OPTIONTYPE = "OptionType";
    public static final String TRADES_UNDERLYING = "Underlying";
    public static final String TRADES_MATURITY = "Maturity";
    public static final String TRADES_MARKETVALUE = "MarketValue";

    public static final String BOOKS_COMPANY = "Company";
    public static final String BOOKS_DESK = "Desk";

    public static final String FX_RATES_BASECCY = "BaseCcy";
    public static final String FX_RATES_COUNTERCCY = "CounterCcy";
    public static final String FX_RATES_FXRATE = "FxRate";

    public static final String NETTINGSETS_NETTINGNAME = "NettingName";
    public static final String NETTINGSETS_NETTINGTYPE = "NettingType";
    public static final String NETTINGSETS_COLLATERAL = "Collateral";
    public static final String NETTINGSETS_MTA = "MTA";
    public static final String NETTINGSETS_INPUTCURRENCY = "InputCurrency";

    public static final String COUNTERPARTIES_COUNTERPARTYNAME = "CounterpartyName";
    public static final String COUNTERPARTIES_RATING = "Rating";
    public static final String COUNTERPARTIES_SECTOR = "Sector";
    public static final String COUNTERPARTIES_COUNTRYOFRISK = "CountryOfRisk";

    /*********************** References names **********************/
    public static final String TRADES_TO_NETTINGSETS = "TradesToNettingSets";
    public static final String TRADES_TO_BOOKS = "TradesToBooks";
    public static final String NETTINGSETS_TO_COUNTERPARTIES = "TradesToCounterParties";

}
