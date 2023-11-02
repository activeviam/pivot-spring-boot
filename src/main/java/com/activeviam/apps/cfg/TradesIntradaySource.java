package com.activeviam.apps.cfg;

import com.qfs.msg.IListenerKey;
import com.qfs.msg.IMessageChannel;
import com.qfs.msg.ISource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TradesIntradaySource implements ISource<String, Object> {
    private static final Logger LOGGER = Logger.getLogger(TradesIntradaySource.class.getSimpleName());
    private BlockingQueue<TradeGenerator.Trade> queue = null;
    private Consumer consumer = null;
    public static final String INTRADAY_TOPIC = "Trades_intraday";

    @Override
    public void fetch(final Collection<? extends IMessageChannel<String, Object>> channels) {
        // not implemented, we're interested only by the real time aspect of this source
        // fetch used for the initial load, this source is not used for that purpose
    }

    @Override
    public List<String> getTopics() {
        return Arrays.asList(INTRADAY_TOPIC);
    }

    @Override
    public String getName() {
        return "TradesIntradaySource";
    }

    @Override
    public IListenerKey listen(final IMessageChannel<String, Object> channel) {
        consumer = new Consumer(channel);
        consumer.start();
        return new IntradayListenerKey(consumer);
    }

    public void setQueue(final BlockingQueue<TradeGenerator.Trade> queue) {
        this.queue = queue;
    }

    public void stop() {
        if (consumer != null) {
            consumer.shutdown();
        }
    }

    /**
     * Consumer consumes the {@link TradeGenerator.Trade} by polling the queue filled by the {@link TradeGenerator}
     *
     */
    private class Consumer extends Thread {
        private final IMessageChannel<String, Object> channel;
        private volatile boolean stop = false;

        public Consumer(final IMessageChannel<String, Object> channel) {
            this.channel = channel;
        }

        public void shutdown() {// stop gracefully the thread
            stop = true;
        }

        @Override
        public void run() {
            final List<TradeGenerator.Trade> trades = new ArrayList<>();
            while (!Thread.currentThread().isInterrupted() && !stop) {
                try {
                    trades.add(queue.take());
                    queue.drainTo(trades);

                    // send the received trades to the store, transaction will happen on the store then
                    channel.sendMessage(INTRADAY_TOPIC, trades);
                    trades.clear();// recycle
                } catch (final InterruptedException e) {
                    LOGGER.log(Level.WARNING, "[TradesIntradaySource] Interrupted.", e);
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    /**
     * IntradayListenerKey provides the ability to cancel the listening process of this source
     *
     */
    protected class IntradayListenerKey implements IListenerKey {
        private final Consumer consumer;

        public IntradayListenerKey(final Consumer consumer) {
            this.consumer = consumer;
        }

        // cancel the polling
        @Override
        public void cancel() {
            consumer.shutdown();
        }

    }
}
