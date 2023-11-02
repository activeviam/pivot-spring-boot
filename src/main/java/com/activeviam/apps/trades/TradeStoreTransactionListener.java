package com.activeviam.apps.trades;

import com.quartetfs.biz.pivot.IActivePivotVersion;
import com.quartetfs.biz.pivot.transaction.IActivePivotSchemaTransactionInfo;
import com.quartetfs.biz.pivot.transaction.IActivePivotTransactionInfo;
import com.quartetfs.biz.pivot.transaction.ITransactionListener;
import com.quartetfs.biz.pivot.transaction.impl.ActivePivotTransactionInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Queue;

public class TradeStoreTransactionListener implements ITransactionListener<IActivePivotTransactionInfo> {
    private final Logger LOGGER = LoggerFactory.getLogger(TradeStoreTransactionListener.class.getName());
    private final Queue<IActivePivotVersion> queue;
    public TradeStoreTransactionListener(Queue<IActivePivotVersion> queue) {
        this.queue=queue;
    }

    @Override
    public void transactionStarted(String transactionManagerId, long transactionId) {

    }

    @Override
    public void transactionRolledBack(String transactionManagerId, long transactionId) {

    }

    @Override
    public void transactionCommitted(String transactionManagerId, long transactionId, IActivePivotTransactionInfo activePivotTransactionInfo) {
        LOGGER.info("added ap version to queue");
        queue.add(activePivotTransactionInfo.getActivePivotVersion());
        LOGGER.info("added ap version to queue");
    }

}
