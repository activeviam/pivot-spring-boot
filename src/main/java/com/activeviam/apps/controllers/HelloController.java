package com.activeviam.apps.controllers;

import com.activeviam.apps.constants.StoreAndFieldConstants;
import com.activeviam.fwk.ActiveViamRuntimeException;
import com.qfs.store.IDatastore;
import com.qfs.store.transaction.ITransactionManager;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.logging.Logger;

@RestController
@RequiredArgsConstructor
public class HelloController {
    private static final Logger LOGGER = Logger.getLogger(HelloController.class.getSimpleName());

    private final IDatastore datastore;

    @GetMapping("/test")
    public String runTest() {
        final ITransactionManager transactionManager = datastore.getTransactionManager();
        int count = 0;
        while (count++ < 10) {
            processLoading(count, transactionManager);
        }
        return "Run Test!";
    }

    private void processLoading(int cycle, ITransactionManager transactionManager) {
        LOGGER.info("processing "+cycle);
        try {
            LOGGER.info("going to start transaction ");
            transactionManager.startTransaction();

            // add record
            addRecord(LocalDate.of(2019, 1, 1), String.valueOf(cycle*10), transactionManager);

            if (cycle > 7)
                throw new ActiveViamRuntimeException("Exit Process Loading");
            LOGGER.info("going to commit Transaction");
            transactionManager.commitTransaction();
        }
        catch (Exception e) {
            try {
                LOGGER.info("going to rollback transaction");
                transactionManager.rollbackTransaction();
                LOGGER.info("successfully rolled back");
            }
            catch (Exception rollbackException) {
                LOGGER.severe("Process Loading " + cycle + " is interrupted");
                rollbackException.printStackTrace();
            }
        }
    }

    private void addRecord(LocalDate date, String tradeId, ITransactionManager transactionManager) {
        Double notional = new Random().nextDouble() * (100D - 1D); // random number between 1 and 100
        var record = new Result(date, tradeId, notional);
        transactionManager.add(StoreAndFieldConstants.TRADES_STORE_NAME, date, tradeId, notional);
        LOGGER.info("added "+record.toString());
    }

    @GetMapping("/hello")
    public String index() { return "Hello from Pivot Spring Boot!"; }

    @AllArgsConstructor
    @ToString
    private static class Result {
        private final LocalDate date;
        private final String tradeId;
        private final Double notional;
    }
}