package com.activeviam.apps.cfg;

import com.activeviam.apps.constants.StoreAndFieldConstants;
import com.qfs.gui.impl.JungSchemaPrinter;
import com.qfs.msg.IMessageChannel;
import com.qfs.msg.csv.ICSVParserConfiguration;
import com.qfs.msg.csv.ICSVSourceConfiguration;
import com.qfs.msg.csv.IFileInfo;
import com.qfs.msg.csv.ILineReader;
import com.qfs.msg.csv.filesystem.impl.FileSystemCSVTopicFactory;
import com.qfs.msg.csv.impl.CSVParserConfiguration;
import com.qfs.msg.csv.impl.CSVSource;
import com.qfs.source.impl.CSVMessageChannelFactory;
import com.qfs.store.IDatastore;
import com.qfs.store.impl.SchemaPrinter;
import com.qfs.util.timing.impl.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.env.Environment;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

@Configuration
public class SourceConfig {

    public static final String TRADES_TOPIC = "Trades";
    public static final String SCENARIOS_TOPIC = "Scenarios";

    private static final Logger logger = LoggerFactory.getLogger(SourceConfig.class);

    @Autowired
    protected Environment env;

    @Autowired
    protected IDatastore datastore;

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
        final var schemaMetadata = datastore.getQueryMetadata().getMetadata();
        final var csvTopicFactory = csvTopicFactory();
        final var csvSource = new CSVSource<Path>();

        final var tradesColumns = schemaMetadata.getFields(StoreAndFieldConstants.TRADES_STORE_NAME);
        final var tradesTopic = csvTopicFactory.createTopic(
                TRADES_TOPIC, env.getProperty("file.trades"), createParserConfig(tradesColumns.size(), tradesColumns));
        csvSource.addTopic(tradesTopic);

        final var scenarioColumns = schemaMetadata.getFields(StoreAndFieldConstants.SCENARIO_STORE_NAME);
        final var scenarioTopic = csvTopicFactory.createTopic(
                SCENARIOS_TOPIC,
                env.getProperty("file.scenarios"),
                createParserConfig(scenarioColumns.size(), scenarioColumns));
        csvSource.addTopic(scenarioTopic);

        final var sourceProps = new Properties();
        sourceProps.put(ICSVSourceConfiguration.PARSER_THREAD_PROPERTY, env.getProperty("parserThreads", "2"));
        sourceProps.put(ICSVSourceConfiguration.SYNCHRONOUS_MODE_PROPERTY, env.getProperty("synchronousMode", "false"));
        csvSource.configure(sourceProps);
        return csvSource;
    }

    @Bean
    public CSVMessageChannelFactory<Path> csvChannelFactory() {
        return new CSVMessageChannelFactory<>(csvSource(), datastore);
    }

    /*
     * **************************** Initial load *********************************
     */

    @Bean
    @DependsOn("startManager")
    public Void initialLoad() {
        final Collection<IMessageChannel<IFileInfo<Path>, ILineReader>> csvChannels = new ArrayList<>();
        csvChannels.add(csvChannelFactory().createChannel(TRADES_TOPIC, StoreAndFieldConstants.TRADES_STORE_NAME));
        csvChannels.add(csvChannelFactory().createChannel(SCENARIOS_TOPIC, StoreAndFieldConstants.SCENARIO_STORE_NAME));

        // do the transactions
        final var before = System.nanoTime();

        datastore.edit(t -> {
            csvSource().fetch(csvChannels);
            t.forceCommit();
        });
        final var elapsed = System.nanoTime() - before;
        logger.info("Initial data load completed in {} ms.", elapsed / 1000000L);

        printStoreSizes();
        return null;
    }

    private ICSVParserConfiguration createParserConfig(final int columnCount, final List<String> columns) {
        final var cfg = columns == null ? new CSVParserConfiguration(columnCount) : new CSVParserConfiguration(columns);
        cfg.setNumberSkippedLines(1); // skip the first line
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
