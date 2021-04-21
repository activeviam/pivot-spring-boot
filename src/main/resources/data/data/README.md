# Data Files

## Trades
Trades contains the following columns
- TradeId	- unique identifier for each Trade
- Product - Product traded
-	NettingSetId - netting set that the trade belongs to
- BookId - indentifier for the trading book that the trade belongs to
- Direction - whether we bought or sold the trade
- InputCurrency - currency for the notional and market value
- Instrument - instrument class eg option, forward swap
- AssetClass - asset class eg FX, IR etc
- SubClass - subclass, used for Commodities and Equities eg oil, metals
- OptionType - if trade is an option, either put or call 
- Underlying - underlying instrument such as currency pair or commodity (*need to add equity underlyings)
- Maturity - maturity or end date of the trade
- Notional - underlying trade amount
- MarketValue - MtM or MV of the trade for today
