package com.activeviam.apps.parquet;

import com.activeviam.apps.constants.StoreAndFieldConstants;
import com.activeviam.fwk.ActiveViamRuntimeException;
import com.activeviam.parquet.IStoreToParquetMapping;
import com.activeviam.parquet.impl.ParquetParserBuilder;
import com.activeviam.parquet.parsers.IParquetFieldParsers;
import com.qfs.store.IDatastore;
import com.qfs.store.NoTransactionException;
import com.qfs.store.transaction.DatastoreTransactionException;
import com.qfs.store.transaction.ITransactionManager;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class ParquetLoaderService {
	private static final Logger LOGGER = Logger.getLogger(ParquetLoaderService.class.getSimpleName());

	private static final int NUM_THREADS_PARSER = 12;
	public static final java.nio.file.Path PARQUET_LOAD_FOLDER =
			FileSystems.getDefault().getPath("src", "main", "resources", "data", "load");
	public static final java.nio.file.Path PARQUET_SAVED_FOLDER =
			FileSystems.getDefault().getPath("src", "main", "resources", "data", "copy");

	private final IDatastore datastore;
	private ExecutorService loadingService = null;
	private volatile boolean isContinueLoading = false;
	private ExecutorService copyFilesService = null;
	private volatile boolean isContinueCopyFiles = false;
	private Random random = new Random();

	public ParquetLoaderService(IDatastore datastore) {
		this.datastore = datastore;
	}

	public void startLoading() {
		LOGGER.info("start Parquet Loading");
		if (loadingService == null) {
			isContinueLoading = true;
			loadingService = Executors.newSingleThreadExecutor();
			Runnable loadingTask = () -> {
				while (isContinueLoading) {
					try {
						loadParquetFiles();
						TimeUnit.MILLISECONDS.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			};

			loadingService.execute(loadingTask);
		}
	}
	public void stopLoading() {
		LOGGER.info("stop Parquet Loading");
		isContinueLoading = false;
		if (loadingService != null) {
			loadingService.shutdown();
			try {
				if (!loadingService.awaitTermination(800, TimeUnit.MILLISECONDS)) {
					loadingService.shutdownNow();
				}
			} catch (InterruptedException e) {
				loadingService.shutdownNow();
			}
			loadingService = null;
		}
	}

	public void startCopyFiles() {
		LOGGER.info("start Parquet files copying");
		if (copyFilesService == null) {
			isContinueCopyFiles = true;
			copyFilesService = Executors.newSingleThreadExecutor();
			Runnable copyFilesTask = () -> {
				while (isContinueCopyFiles) {
					try {
						copyParquetFiles();
						TimeUnit.MILLISECONDS.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			};

			copyFilesService.execute(copyFilesTask);
		}
	}

	public void stopCopyFiles() {
		LOGGER.info("stop Parquet files copying");
		isContinueCopyFiles = false;
		if (copyFilesService != null) {
			copyFilesService.shutdown();
			try {
				if (!copyFilesService.awaitTermination(800, TimeUnit.MILLISECONDS)) {
					copyFilesService.shutdownNow();
				}
			} catch (InterruptedException e) {
				copyFilesService.shutdownNow();
			}
			copyFilesService = null;
		}
	}

	private void loadParquetFiles() {
		final ITransactionManager transactionManager = datastore.getTransactionManager();
		try {
			transactionManager.startTransaction(StoreAndFieldConstants.TRADES_STORE_NAME);
			final var storeName = StoreAndFieldConstants.TRADES_STORE_NAME;
			final var storePath = new org.apache.hadoop.fs.Path(PARQUET_LOAD_FOLDER.resolve("trades03.parquet").toString());

			// If the storePath provided is too high-level (parent level) e.g. bucket level,
			// consider setting recursive to false to prevent dive diving into unwanted subdirectories
//			LOGGER.info("Processing parquet file from [" + storePath + "]");
			new ParquetParserBuilder(datastore)
					.withNumberOfThreads(NUM_THREADS_PARSER)
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
//			LOGGER.info("Commit Transaction");
			transactionManager.commitTransaction();
		} catch (DatastoreTransactionException e) {
			LOGGER.warning("DatastoreTransactionException");
			e.printStackTrace();
			try {
//				LOGGER.info("going to rollback transaction");
				transactionManager.rollbackTransaction();
//				LOGGER.info("successfully rolled back");
			}
			catch (Exception rollbackException) {
				LOGGER.severe("Rollback failed");
				rollbackException.printStackTrace();
			}
		} catch (NoTransactionException | IOException e) {
			e.printStackTrace();
			try {
//				LOGGER.info("going to rollback transaction");
				transactionManager.rollbackTransaction();
//				LOGGER.info("successfully rolled back");
			}
			catch (Exception rollbackException) {
				LOGGER.severe("Rollback failed");
				rollbackException.printStackTrace();
			}
		} catch (ActiveViamRuntimeException e) {
			LOGGER.warning("ActiveViamRuntimeException");
			e.printStackTrace();
			// If don't rollback, will lead to DatastoreTransactionException when it tries to startTransaction next cycle
			try {
//				LOGGER.info("going to rollback transaction");
				transactionManager.rollbackTransaction();
//				LOGGER.info("successfully rolled back");
			}
			catch (Exception rollbackException) {
				LOGGER.severe("Rollback failed");
				rollbackException.printStackTrace();
			}
		} catch (IllegalStateException e) {
			LOGGER.severe("IllegalStateException");
			e.printStackTrace();
			try {
//				LOGGER.info("going to rollback transaction");
				transactionManager.rollbackTransaction();
//				LOGGER.info("successfully rolled back");
			}
			catch (Exception rollbackException) {
				LOGGER.severe("Rollback failed");
				rollbackException.printStackTrace();
			}
		}
	}


	/**
	 * Copy files from saved to load folder
	 * Randomly delete one of the file in load folder to trigger rollback
	 */
	private void copyParquetFiles() {
		try {
//			LOGGER.info("Copy trades01.parquet");
			Files.copy(
					PARQUET_SAVED_FOLDER.resolve("trades01.parquet"),
					PARQUET_LOAD_FOLDER.resolve("trades01.parquet"),
					StandardCopyOption.REPLACE_EXISTING);

//			LOGGER.info("Copy trades02.parquet");
			Files.copy(
					PARQUET_SAVED_FOLDER.resolve("trades02.parquet"),
					PARQUET_LOAD_FOLDER.resolve("trades02.parquet"),
					StandardCopyOption.REPLACE_EXISTING);

			if (randomEventHappen()) {
				LOGGER.info("Delete trades03.parquet");
				// remove trades03.parquet
				Files.deleteIfExists(PARQUET_LOAD_FOLDER.resolve("trades03.parquet"));
			}
			else {
//				LOGGER.info("Copy trades03.parquet");
				Files.copy(
						PARQUET_SAVED_FOLDER.resolve("trades03.parquet"),
						PARQUET_LOAD_FOLDER.resolve("trades03.parquet"),
						StandardCopyOption.REPLACE_EXISTING);
			}
		} catch (IOException e) {
			LOGGER.severe("Failed to copy (or delete) parquet files.");
			e.printStackTrace();
		}
	}

	// 50% chance
	private boolean randomEventHappen() {
		return Math.random() < 0.5;
	}

}
