package com.activeviam.apps.cfg;

import com.activeviam.apps.constants.StoreAndFieldConstants;
import com.qfs.gui.impl.JungSchemaPrinter;
import com.qfs.msg.IMessageChannel;
import com.qfs.msg.csv.*;
import com.qfs.msg.csv.filesystem.impl.FileSystemCSVTopicFactory;
import com.qfs.msg.csv.impl.CSVParserConfiguration;
import com.qfs.msg.csv.impl.CSVSource;
import com.qfs.source.impl.*;
import com.qfs.store.IDatastore;
import com.qfs.store.IDatastoreSchemaMetadata;
import com.qfs.store.impl.SchemaPrinter;
import com.qfs.util.timing.impl.StopWatch;
import com.quartetfs.fwk.monitoring.jmx.impl.JMXEnabler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.env.Environment;

import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.activeviam.apps.constants.StoreAndFieldConstants.TRADES_STORE_NAME;

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


        final List<String> tradesColumns = schemaMetadata.getFields(TRADES_STORE_NAME);
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
    @DependsOn("startManager")
    public Void initialLoad() {
        final Collection<IMessageChannel<IFileInfo<Path>, ILineReader>> csvChannels = new ArrayList<>();
        csvChannels.add(csvChannelFactory().createChannel(TRADES_TOPIC, TRADES_STORE_NAME));

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

    /*
     * **************************** Realtime *********************************
     */
    @Bean
    @DependsOn("initialLoad")
    public Void realTime() {
        final Map<String, String> topicToStoreMapping = new HashMap<>() {
            private static final long serialVersionUID = 1L;
            {
                put(TRADES_TOPIC, TRADES_STORE_NAME);
            }
        };
        // listen to the updates for each of the stores fed with csv
        // enable the autoCommit on each existing csv channel
        final var listen = new Listen<>(csvChannelFactory(), topicToStoreMapping);
        // listen to the updates
        listen.listen(csvSource());

        // set the intraday source that listens to POJO
        final var intradaySource = intradaySource();
        intradaySource.listen(intradayChannel());
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

    /*
     * **************************** Intraday Source *********************************
     */
    @Bean
    public BlockingQueue<TradeGenerator.Trade> queue() {
        return new LinkedBlockingQueue<>();
    }

    @Bean(destroyMethod = "stop")
    public TradesIntradaySource intradaySource() {
        final var intradaySource = new TradesIntradaySource();
        intradaySource.setQueue(queue());
        return intradaySource;
    }

    @Bean
    public IMessageChannel<String, Object> intradayChannel() {
        final var pojoChannelFactory = new POJOMessageChannelFactory(datastore);

        return pojoChannelFactory.createChannel(TRADES_TOPIC, TRADES_STORE_NAME)
                .accumulate()
                .withPublisher(new AutoCommitTuplePublisher<>(new TuplePublisher<String>(datastore, TRADES_STORE_NAME)));
    }

    @Bean
    public TradeGenerator tradeGenerator() {
        final var generator = new TradeGenerator();
        generator.setQueue(queue());
        return generator;
    }

    @Bean
    public JMXEnabler jmxAddTrade() {
        final var jmx = new JMXEnabler();
        jmx.setName("Training Generator");
        jmx.setMonitoredComponent(tradeGenerator());
        return jmx;
    }
}
