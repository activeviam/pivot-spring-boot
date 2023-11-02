package com.activeviam.apps.trades;

import com.qfs.store.IDatastore;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;

import java.time.LocalDate;
import java.util.logging.Logger;

import static com.activeviam.apps.constants.StoreAndFieldConstants.TRADES_STORE_NAME;

@ManagedResource
public class TradeInserterService {

    private static final Logger LOGGER = Logger.getLogger(TradeInserterService.class.getSimpleName());

    private IDatastore datastore;
    public TradeInserterService(IDatastore datastore) {
        this.datastore=datastore;
    }
    @ManagedOperation
    public void insertTrade(final String tradeId, final double notional) {
        final var tuple = new Object[]{tradeId, notional};
        try{
            final var transactionManager = datastore.getTransactionManager();
            transactionManager.startTransaction(new int[]{0});
            transactionManager.add(TRADES_STORE_NAME, tuple);
            transactionManager.commitTransaction();
        }catch (Exception e){

        }
        LOGGER.info("generated a trade");
    }
}
