package com.activeviam.apps.pivotspringboot.activepivot;

import com.qfs.gui.impl.JungSchemaPrinter;
import com.qfs.msg.IMessageChannel;
import com.qfs.msg.csv.*;
import com.qfs.msg.csv.filesystem.impl.FileSystemCSVTopicFactory;
import com.qfs.msg.csv.impl.CSVParserConfiguration;
import com.qfs.msg.csv.impl.CSVSource;
import com.qfs.source.impl.CSVMessageChannelFactory;
import com.qfs.store.IDatastore;
import com.qfs.store.IDatastoreSchemaMetadata;
import com.qfs.store.impl.SchemaPrinter;
import com.qfs.util.timing.impl.StopWatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

@Configuration
public class SourceConfig {

    private static final Logger LOGGER = Logger.getLogger(SourceConfig.class.getSimpleName());

    @Autowired
    protected Environment env;

    @Autowired
    protected IDatastore datastore;

    public static final String TRADES_TOPIC = "Trades";

    /*
     * **************************** CSV Source *********************************
     */

    /**
     * Topic factory bean. Allows to create CSV topics and watch changes to directories. Autocloseable.
     *
     * @return the topic factory
     */
    @Bean
    public FileSystemCSVTopicFactory csvTopicFactory() {
        return new FileSystemCSVTopicFactory(false);
    }

    @Bean(destroyMethod = "close")
    public CSVSource<Path> csvSource() {
        final IDatastoreSchemaMetadata schemaMetadata = datastore.getSchemaMetadata();
        final FileSystemCSVTopicFactory csvTopicFactory = csvTopicFactory();
        final CSVSource<Path> csvSource = new CSVSource<>();


        final List<String> tradesColumns = schemaMetadata.getFields(StoreAndFieldConstants.TRADES_STORE_NAME);
        final ICSVTopic<Path> tradesTopic = csvTopicFactory.createTopic(TRADES_TOPIC, env.getProperty("file.trades"),
                createParserConfig(tradesColumns.size(), tradesColumns));
        csvSource.addTopic(tradesTopic);

        final Properties sourceProps = new Properties();
        sourceProps.put(ICSVSourceConfiguration.PARSER_THREAD_PROPERTY, env.getProperty("parserThreads", "2"));
        sourceProps.put(ICSVSourceConfiguration.SYNCHRONOUS_MODE_PROPERTY, env.getProperty("synchronousMode", "false"));
        csvSource.configure(sourceProps);
        return csvSource;
    }

    @Bean
    public CSVMessageChannelFactory<Path> csvChannelFactory() {
        final CSVMessageChannelFactory<Path> csvChannelFactory = new CSVMessageChannelFactory<>(csvSource(), datastore);

        return csvChannelFactory;
    }

    /*
     * **************************** Initial load *********************************
     */

    @Bean
    public Void initialLoad() {
        final Collection<IMessageChannel<IFileInfo<Path>, ILineReader>> csvChannels = new ArrayList<>();
        csvChannels.add(csvChannelFactory().createChannel(TRADES_TOPIC, StoreAndFieldConstants.TRADES_STORE_NAME));

        // do the transactions
        final long before = System.nanoTime();

        datastore.edit(t -> {
            csvSource().fetch(csvChannels);
            t.forceCommit();
        });
        final long elapsed = System.nanoTime() - before;
        LOGGER.log(Level.INFO, "Initial data load completed in " + elapsed / 1000000L + "ms");

        printStoreSizes();
        return null;
    }

    private ICSVParserConfiguration createParserConfig(final int columnCount, final List<String> columns) {
        final CSVParserConfiguration cfg = columns == null ? new CSVParserConfiguration(columnCount) : new CSVParserConfiguration(columns);
        cfg.setNumberSkippedLines(1);// skip the first line
        return cfg;
    }
    private void printStoreSizes() {
        // add some logging
        if (Boolean.parseBoolean(env.getProperty("schema.printer", "true"))) {
            // display the graph
        	System.setProperty("java.awt.headless", "false");
            new JungSchemaPrinter(false).print("Datastore", datastore);
        }

        // Print stop watch profiling
        StopWatch.get().printTimings();
        StopWatch.get().printTimingLegend();

        // print sizes
        SchemaPrinter.printStoresSizes(datastore.getHead().getSchema());
    }
}
