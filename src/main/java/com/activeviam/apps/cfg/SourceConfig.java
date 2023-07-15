package com.activeviam.apps.cfg;

import com.activeviam.apps.constants.StoreAndFieldConstants;
import com.qfs.msg.csv.ICSVParserConfiguration;
import com.qfs.msg.csv.filesystem.impl.FileSystemCSVTopicFactory;
import com.qfs.msg.csv.impl.CSVParserConfiguration;
import com.qfs.msg.csv.impl.CSVSource;
import com.qfs.msg.csv.impl.CSVSourceConfiguration;
import com.qfs.platform.IPlatform;
import com.qfs.source.impl.CSVMessageChannelFactory;
import com.qfs.store.IDatastore;
import java.nio.file.Path;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class SourceConfig {

    private static final Logger logger = LoggerFactory.getLogger(SourceConfig.class);

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
        final var schemaMetadata = datastore.getQueryMetadata().getMetadata();
        final var csvTopicFactory = csvTopicFactory();
        final var csvSource = new CSVSource<Path>();

        final var tradesColumns = schemaMetadata.getFields(StoreAndFieldConstants.TRADES_STORE_NAME);
        final var tradesTopic = csvTopicFactory.createTopic(
                TRADES_TOPIC, env.getProperty("file.trades"), createParserConfig(tradesColumns.size(), tradesColumns));
        csvSource.addTopic(tradesTopic);

        // Allocate half the the machine cores to CSV parsing
        Integer parserThreads = Math.max(2, IPlatform.CURRENT_PLATFORM.getProcessorCount() / 2);
        logger.info("Allocating " + parserThreads + " parser threads.");

        CSVSourceConfiguration.CSVSourceConfigurationBuilder<Path> sourceConfigurationBuilder =
                new CSVSourceConfiguration.CSVSourceConfigurationBuilder<>();
        sourceConfigurationBuilder.parserThreads(parserThreads);
        sourceConfigurationBuilder.synchronousMode(Boolean.valueOf(env.getProperty("synchronousMode", "false")));
        csvSource.configure(sourceConfigurationBuilder.build());
        return csvSource;
    }

    @Bean
    public CSVMessageChannelFactory<Path> csvChannelFactory() {
        return new CSVMessageChannelFactory<>(csvSource(), datastore);
    }

    private ICSVParserConfiguration createParserConfig(final int columnCount, final List<String> columns) {
        final var cfg = columns == null ? new CSVParserConfiguration(columnCount) : new CSVParserConfiguration(columns);
        cfg.setNumberSkippedLines(1); // skip the first line
        return cfg;
    }
}
