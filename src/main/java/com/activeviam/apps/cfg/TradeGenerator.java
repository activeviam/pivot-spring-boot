package com.activeviam.apps.cfg;

import com.qfs.jmx.JmxOperation;
import com.quartetfs.fwk.format.impl.LocalDateParser;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Logger;

public class TradeGenerator {

    private static final Logger LOGGER = Logger.getLogger(TradeGenerator.class.getSimpleName());
    private BlockingQueue<Trade> queue = null;
    private static final String SOURCE_NAME = "POJO Source";

    @JmxOperation(desc = "generateTrade", params = { "asOfDate", "tradeId", "notional" })
    public void generateTrade(final String asOfDate, final String tradeId, final double notional) {
        final LocalDate tradeDate = LocalDate.parse(asOfDate, DateTimeFormatter.ofPattern(LocalDateParser.DEFAULT_PATTERN));
        final List<Trade> generatedTrades = Collections.singletonList(new Trade(tradeDate, tradeId, notional));
        queue.addAll(generatedTrades);
        LOGGER.info("generated a trade");
    }

    public void setQueue(final BlockingQueue<Trade> queue) {
        this.queue = queue;
    }

    /**
     * The POJO we send
     *
     */
    @Getter
    @AllArgsConstructor
    public static class Trade {
        private final LocalDate asOfDate;
        private final String tradeID;
        private final double notional;
    }
}
