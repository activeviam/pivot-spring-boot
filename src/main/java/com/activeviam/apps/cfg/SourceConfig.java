/*
 * (C) ActiveViam 2021
 * ALL RIGHTS RESERVED. This material is the CONFIDENTIAL and PROPRIETARY
 * property of ActiveViam. Any unauthorized use,
 * reproduction or transfer of this material is strictly prohibited
 */

package com.activeviam.apps.cfg;

import com.activeviam.apps.constants.StoreAndFieldConstants;
import com.activeviam.cloud.aws.s3.entity.impl.S3CloudDirectory;
import com.activeviam.cloud.aws.s3.fetch.impl.S3ObjectChannel;
import com.activeviam.cloud.aws.s3.impl.BucketUtil;
import com.activeviam.cloud.entity.ICloudEntityPath;
import com.activeviam.cloud.fetch.impl.CloudFetchingConfig;
import com.activeviam.cloud.msg.csv.impl.AwsCsvDataProviderFactory;
import com.activeviam.cloud.source.csv.ICloudCsvDataProviderFactory;
import com.activeviam.cloud.source.csv.impl.CloudDirectoryCSVTopic;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3Object;
import com.qfs.msg.csv.ICSVTopic;
import com.qfs.msg.csv.IFileInfo;
import com.qfs.msg.csv.ILineReader;
import com.qfs.msg.csv.impl.CSVParserConfiguration;
import com.qfs.msg.csv.impl.CSVSource;
import com.qfs.source.impl.CSVMessageChannelFactory;
import com.qfs.source.impl.Fetch;
import com.qfs.store.IDatastore;
import com.qfs.store.IDatastoreSchemaMetadata;
import com.qfs.store.impl.SchemaPrinter;
import com.qfs.util.timing.impl.StopWatch;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.env.Environment;

@Configuration
public class SourceConfig {

    private static final Logger LOGGER = Logger.getLogger(SourceConfig.class.getSimpleName());

    @Autowired
    protected Environment env;

    @Autowired
    protected IDatastore datastore;

    protected CloudFetchingConfig config;
    protected S3CloudDirectory directory;

    public static final String TRADES_TOPIC = "Trades";

    private static final String BUCKET = "rnd-benchmark-test-storage";
    private static final Regions REGION = Regions.EU_WEST_3;

    /**
     * [Bean].
     *
     * @return Client to connect to AWS S3
     */
    @Bean
    public AmazonS3 client() {
        return BucketUtil.getClient(REGION);
    }

    /*
     * **************************** CSV Source *********************************
     */

    /**
     * Topic factory bean. Allows to create CSV topics and watch changes to directories.
     * Autocloseable.
     *
     * @return the topic factory
     */
    @Bean
    public ICloudCsvDataProviderFactory<S3Object> topicFactory() {
        this.config = new CloudFetchingConfig(2);
        return new AwsCsvDataProviderFactory(this.config);
    }

    @Bean(destroyMethod = "close")
    public CSVSource<ICloudEntityPath<S3Object>> cloudSource() {
        final IDatastoreSchemaMetadata schemaMetadata = datastore.getSchemaMetadata();

        //Create the directory
        this.directory = new S3CloudDirectory(client(), BUCKET, "");

        //Fetch all data
        ICSVTopic<ICloudEntityPath<S3Object>> directoryTopic =
            new CloudDirectoryCSVTopic<>(
                "Session",
                new CSVParserConfiguration(
                    datastore.getSchemaMetadata()
                        .getStoreMetadata(StoreAndFieldConstants.SESSION_STORE_NAME)
                        .getStoreFormat()
                        .getRecordFormat()
                        .getFieldCount()),
                topicFactory(),
                this.directory,
                "[a-z,A-Z,0-9,\\,/]*(session.csv)$");

        final CSVSource<ICloudEntityPath<S3Object>> csvSource = new CSVSource<>();
        csvSource.addTopic(directoryTopic);

        return csvSource;
    }

    /*
     * **************************** Initial load *********************************
     */

    @Bean
    @DependsOn("startManager")
    public Void initialLoad() {
        final Collection<S3ObjectChannel> csvChannels = new ArrayList<>();
        directory.listEntities(true).forEach(
            cloudEntity -> csvChannels.add(new S3ObjectChannel(cloudEntity, this.config)));

        // Do the transactions :
        final long before = System.nanoTime();
        final Fetch<IFileInfo<ICloudEntityPath<S3Object>>, ILineReader> load = new Fetch<>(
            csvChannelFactory(),
            List.of(StoreAndFieldConstants.SESSION_STORE_NAME));

        load.fetch(cloudSource());

        final long elapsed = System.nanoTime() - before;
        LOGGER.log(Level.INFO, "Initial data load completed in " + elapsed / 1000000L + "ms");

        printStoreSizes();
        return null;
    }

    private CSVMessageChannelFactory<ICloudEntityPath<S3Object>> csvChannelFactory() {
        return new CSVMessageChannelFactory<>(cloudSource(), datastore);
    }

    private void printStoreSizes() {
        // Print stop watch profiling
        StopWatch.get().printTimings();
        StopWatch.get().printTimingLegend();

        // print sizes
        SchemaPrinter.printStoresSizes(datastore.getHead().getSchema());
    }
}
