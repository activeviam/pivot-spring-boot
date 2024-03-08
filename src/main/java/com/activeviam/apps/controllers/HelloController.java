package com.activeviam.apps.controllers;

import com.activeviam.apps.constants.StoreAndFieldConstants;
import com.activeviam.fwk.ActiveViamRuntimeException;
import com.activeviam.parquet.IStoreToParquetMapping;
import com.activeviam.parquet.impl.ParquetParserBuilder;
import com.activeviam.parquet.parsers.IParquetFieldParsers;
import com.qfs.store.IDatastore;
import com.qfs.store.NoTransactionException;
import com.qfs.store.transaction.DatastoreTransactionException;
import com.qfs.store.transaction.ITransactionManager;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.logging.Logger;

@RestController
@RequiredArgsConstructor
public class HelloController {
    private static final Logger LOGGER = Logger.getLogger(HelloController.class.getSimpleName());

    private final IDatastore datastore;

    @GetMapping("/parquet")
    public String runParquet() {
        final ITransactionManager transactionManager = datastore.getTransactionManager();
		try {
			transactionManager.startTransaction(StoreAndFieldConstants.TRADES_STORE_NAME);
            Map<String, Path> parsePaths = getParserPaths();
            parsePaths.forEach((storeName, storePath) -> {
                try {
                        // If the storePath provided is too high-level (parent level) e.g. bucket level,
                        // consider setting recursive to false to prevent dive diving into unwanted subdirectories
                        LOGGER.info("Processing parquet file from [" + storePath + "]");
                        new ParquetParserBuilder(datastore)
                                .withNumberOfThreads(2) // TODO configure number of threads for parquet parser
                                .build()
                                .parse(storePath,
                                        new Configuration(false),
                                        (IParquetFieldParsers) null,
                                        IStoreToParquetMapping.builder().onStore(storeName)
                                                .feedStoreField(StoreAndFieldConstants.ASOFDATE)
                                                .withColumnCalculator()
                                                .fromSingleParquetField("AsOf")
                                                .map(a -> LocalDate.parse((String) a, DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                                                .feedStoreField(StoreAndFieldConstants.TRADES__TRADEID)
                                                .withParquetField("TradeId")
                                                .feedStoreField(StoreAndFieldConstants.TRADES__NOTIONAL)
                                                .withColumnCalculator()
                                                .fromSingleParquetField("Notional")
                                                .map(a -> Double.parseDouble((String) a))
                                                .build()
                                );
                } catch (FileNotFoundException e) {
                    LOGGER.warning("File not found " + storePath.toString());
                } catch (NoTransactionException | IOException e) {
                    throw new ActiveViamRuntimeException("An error occurred when reading parquet source", e);
                }
            });
            transactionManager.commitTransaction();
		} catch (DatastoreTransactionException | IOException e) {
			throw new RuntimeException(e);
		}

		return "Run Parquet!";
    }

    private Map<String, org.apache.hadoop.fs.Path> getParserPaths() throws UnsupportedEncodingException, FileNotFoundException{
        Map<String, org.apache.hadoop.fs.Path> storePaths = new HashMap<>();

        org.apache.hadoop.fs.Path tradesPath = fetchParquetDirectory("data/parquet");
        storePaths.put(StoreAndFieldConstants.TRADES_STORE_NAME, tradesPath);
        return storePaths;
    }

    private org.apache.hadoop.fs.Path fetchParquetDirectory(String directory) throws FileNotFoundException {
        File parquetDir = new File(directory);
        if(!parquetDir.exists()){
            parquetDir = ResourceUtils.getFile("classpath:" + directory);
        }
        return new org.apache.hadoop.fs.Path(parquetDir.getPath());
    }


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
        var record = new Trade(date, tradeId, notional);
        transactionManager.add(StoreAndFieldConstants.TRADES_STORE_NAME, date, tradeId, notional);
        LOGGER.info("added "+record.toString());
    }

    @GetMapping("/hello")
    public String index() { return "Hello from Pivot Spring Boot!"; }

    @AllArgsConstructor
    @ToString
    private static class Trade {
        private final LocalDate date;
        private final String tradeId;
        private final Double notional;
    }
}