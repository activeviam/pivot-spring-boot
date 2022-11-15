package com.activeviam.apps.activepivot.data.source;

import com.activeviam.fwk.ActiveViamRuntimeException;
import com.qfs.msg.IMessageChannel;
import com.qfs.msg.csv.ICSVParserConfiguration;
import com.qfs.msg.csv.ICSVTopic;
import com.qfs.msg.csv.IFileInfo;
import com.qfs.msg.csv.ILineReader;
import com.qfs.msg.csv.filesystem.impl.FileSystemCSVTopicFactory;
import com.qfs.msg.csv.impl.CSVParserConfiguration;
import com.qfs.msg.csv.impl.CSVSource;
import com.qfs.source.impl.CSVMessageChannelFactory;
import com.qfs.store.IDatastore;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.activeviam.apps.activepivot.data.datastore.StoreAndFieldConstants.TRADES_DETAILS_STORE_NAME;
import static com.activeviam.apps.activepivot.data.datastore.StoreAndFieldConstants.TRADES_STORE_NAME;

@Service
@Slf4j
@EnableConfigurationProperties(CsvDataFilesProperties.class)
public class DataLoadingService {

    private final CsvDataFilesProperties csvDataProperties;

    private final IDatastore datastore;

    private final ResourceLoader resourceLoader;

    private final FileSystemCSVTopicFactory csvTopicFactory = new FileSystemCSVTopicFactory(false);

    private final CSVSource<Path> csvSource = new CSVSource<>();

    private final CSVMessageChannelFactory<Path> csvChannelFactory;

    public DataLoadingService(
            CsvDataFilesProperties csvDataProperties, IDatastore datastore, ResourceLoader resourceLoader) {
        this.csvDataProperties = csvDataProperties;
        this.datastore = datastore;
        this.resourceLoader = resourceLoader;
        try {
            // TODO: we could do this for every file
            csvSource.addTopic(createTopic(TRADES_STORE_NAME, TRADES_STORE_NAME));
            csvSource.addTopic(createTopic(TRADES_DETAILS_STORE_NAME, TRADES_DETAILS_STORE_NAME));
            csvSource.configure(csvDataProperties.getCsvSourceProperties().toProperties());
        } catch (IOException e) {
            throw new ActiveViamRuntimeException("Failed to create CSV sources", e);
        }
        csvChannelFactory = new CSVMessageChannelFactory<>(csvSource, datastore);
    }

    private ICSVTopic<Path> createTopic(String name, String store) throws IOException {
        final var file = resourceLoader
                .getResource(csvDataProperties.getFiles().get(name))
                .getFile()
                .getAbsolutePath();
        final var columns = datastore.getQueryMetadata().getMetadata().getFields(store);
        return csvTopicFactory.createTopic(name, file, createParserConfig(columns.size(), columns));
    }

    private ICSVParserConfiguration createParserConfig(final int columnCount, final List<String> columns) {
        final var cfg = columns == null ? new CSVParserConfiguration(columnCount) : new CSVParserConfiguration(columns);
        if (csvDataProperties.isHeaderRow()) {
            cfg.setNumberSkippedLines(1); // skip the first line
        }
        return cfg;
    }

    public void loadData() {
        // do the transactions
        final var before = System.nanoTime();
        final Collection<IMessageChannel<IFileInfo<Path>, ILineReader>> csvChannels = new ArrayList<>();
        csvChannels.add(csvChannelFactory.createChannel(TRADES_STORE_NAME, TRADES_STORE_NAME));
        csvChannels.add(csvChannelFactory.createChannel(TRADES_DETAILS_STORE_NAME, TRADES_DETAILS_STORE_NAME));
        datastore.edit(t -> {
            csvSource.fetch(csvChannels);
            t.forceCommit();
        });
        final var elapsed = System.nanoTime() - before;
        log.info("Data load completed in {} ms.", elapsed / 1000000L);
    }

}
