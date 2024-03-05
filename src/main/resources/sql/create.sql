CREATE TABLE IF NOT EXISTS TradePnL
(
    AsOfDate         DATE         NOT NULL,
    TradeId          VARCHAR(100) NOT NULL,
    ScenarioSet      VARCHAR(100),
    CalculationId    VARCHAR(100),
    RiskFactor       VARCHAR(100),
    RiskClass        VARCHAR(100),
    SensitivityName  VARCHAR(100),
    LiquidityHorizon INT,
    Ccy              VARCHAR(100),
    MTM              DOUBLE,
--     "PnL[]"          DOUBLE ARRAY,
    PRIMARY KEY (AsOfDate, TradeId)
);
