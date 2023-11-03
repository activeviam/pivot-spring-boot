package com.activeviam.apps.trades;

import com.qfs.store.IDatastore;
import com.quartetfs.biz.pivot.IActivePivot;
import com.quartetfs.biz.pivot.IActivePivotManager;
import com.quartetfs.biz.pivot.IActivePivotVersion;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;

import java.time.LocalDate;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Logger;

import static com.activeviam.apps.constants.StoreAndFieldConstants.TRADES_STORE_NAME;

@ManagedResource
public class TradeInserterService {

    private static final Logger LOGGER = Logger.getLogger(TradeInserterService.class.getSimpleName());

    private final IDatastore datastore;
    private final BlockingQueue<IActivePivotVersion> activePivotVersionQueue;
    private final IActivePivotManager activePivotManager;
    public TradeInserterService(IActivePivotManager activePivotManager, IDatastore datastore, BlockingQueue<IActivePivotVersion> activePivotVersionQueue) {
        this.activePivotManager=activePivotManager;
        this.datastore=datastore;
        this.activePivotVersionQueue=activePivotVersionQueue;
    }
    @ManagedOperation
    public void insertTrade(final String tradeId, final double notional) {
        final var tuple = new Object[]{tradeId, notional};
        try{
            final var transactionManager = datastore.getTransactionManager();
            transactionManager.startTransaction(new int[]{0});
            transactionManager.add(TRADES_STORE_NAME, tuple);
            transactionManager.commitTransaction();

            activePivotVersionQueue.add(activePivotManager.getActivePivots().get("Cube").getHead());
            LOGGER.info("added ap version to queue");
        }catch (Exception e){

        }
        LOGGER.info("generated a trade");
    }
}
