/*
 * (C) ActiveViam 2007-2020
 * ALL RIGHTS RESERVED. This material is the CONFIDENTIAL and PROPRIETARY
 * property of ActiveViam. Any unauthorized use,
 * reproduction or transfer of this material is strictly prohibited
 */

package com.activeviam.parquet;

import com.activeviam.fwk.ActiveViamRuntimeException;
import com.activeviam.parquet.IParquetParser;
import com.activeviam.parquet.IParquetParserBuilder;
import com.activeviam.parquet.IStoreToParquetMapping;
import com.activeviam.parquet.IStoreToParquetMappingBuilder.INamedStoreToParquetMappingBuilder;
import com.activeviam.parquet.impl.ParquetParserBuilder;
import com.activeviam.parquet.parsers.IParquetFieldParsers;
import com.qfs.condition.impl.BaseConditions;
import com.qfs.literal.ILiteralType;
import com.qfs.store.IDatastore;
import com.qfs.store.NoTransactionException;
import com.qfs.store.query.IDictionaryCursor;
import com.qfs.util.impl.QfsFileTestUtils;
import com.qfs.util.impl.ThrowingLambda.ThrowingBiConsumer;
import com.qfs.vector.IVector;
import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.avro.Schema;
import org.apache.avro.Schema.Type;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericData.Record;
import org.apache.avro.specific.SpecificData;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.mapreduce.RecordWriter;
import org.apache.parquet.avro.AvroSchemaConverter;
import org.apache.parquet.avro.AvroWriteSupport;
import org.apache.parquet.hadoop.ParquetFileWriter.Mode;
import org.apache.parquet.hadoop.ParquetOutputFormat;
import org.apache.parquet.hadoop.metadata.CompressionCodecName;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Assertions;

/**
 * Utilities for parquet test.
 *
 * @author ActiveViam
 */
public class ParquetTestUtils {

	/**
	 * Create a parquet file with contents.
	 *
	 * @param filePath filePath where the file is created
	 * @param schema schema of the data
	 * @param recordToWrite records to write
	 */
	public static void writeParquetFile(
			final String filePath,
			final Schema schema,
			final Collection<Record> recordToWrite)
			throws IOException,
				InterruptedException {
		writeParquetFile(Paths.get(filePath), schema, recordToWrite);
	}

	/**
	 * Create a parquet file with contents.
	 *
	 * @param filePath filePath where the file is created
	 * @param schema schema of the data
	 * @param recordToWrite records to write
	 */
	public static void writeParquetFile(
			final Path filePath,
			final Schema schema,
			final Collection<Record> recordToWrite)
			throws IOException,
				InterruptedException {
		writeParquetFile(filePath, schema, recordToWrite, new Configuration(), CompressionCodecName.UNCOMPRESSED);
	}

	/**
	 * Create a parquet file with contents.
	 *
	 * @param filePath filePath where the file is created
	 * @param schema schema of the data
	 * @param recordsToWrite records to write
	 * @param configuration the hadoop configuration to use for the writer
	 * @param compressionCodec the codec for the compression scheme to use for the parquet file
	 */
	public static void writeParquetFile(
			final Path filePath,
			final Schema schema,
			final Collection<Record> recordsToWrite,
			final Configuration configuration,
			final CompressionCodecName compressionCodec)
			throws IOException,
				InterruptedException {
		try {
			Files.deleteIfExists(filePath);
		} catch (final NoSuchFileException x) {
			throw new ActiveViamRuntimeException(String.format("%s: no such file or directory.", filePath), x);
		} catch (final DirectoryNotEmptyException x) {
			throw new ActiveViamRuntimeException(String.format("%s not empty.", filePath), x);
		} catch (final IOException x) {
			throw new ActiveViamRuntimeException(x);
		}

		final org.apache.hadoop.fs.Path path = new org.apache.hadoop.fs.Path(filePath.toAbsolutePath().toString());

		RecordWriter<Void, Record> writer = null;
		try {
			writer = new ParquetOutputFormat<Record>(
					new AvroWriteSupport<>(
							new AvroSchemaConverter(configuration).convert(schema),
							schema,
							SpecificData.get()))
									.getRecordWriter(configuration, path, compressionCodec, Mode.CREATE);
			for (final Record record : recordsToWrite) {
				writer.write(null, record);
			}
		} finally {
			if (writer != null) {
				writer.close(null);
			}
		}
	}

	/**
	 * Write parquet folder.
	 *
	 * @param folderPath the folder path
	 * @param schema the schema
	 * @param recordToWrite the records to write.
	 * @param recordPerFile the number of records per file.
	 */
	public static void writeParquetFolder(
			final Path folderPath,
			final Schema schema,
			final Collection<Record> recordToWrite,
			final int recordPerFile)
			throws IOException,
				InterruptedException {
		if (folderPath.toFile().exists()) {
			QfsFileTestUtils.deleteDirectory(folderPath);
		}

		Files.createDirectory(folderPath);
		final List<Record> currentRecord = new ArrayList<>();
		int position = 0;
		for (final Record record : recordToWrite) {
			currentRecord.add(record);
			position++;
			if (position % recordPerFile == 0) {
				final Path path = folderPath.resolve(position + ".parquet");
				writeParquetFile(path, schema, currentRecord);
				currentRecord.clear();
			}
		}

		// Flush the remaining records
		if (currentRecord.size() > 0) {
			final Path path = folderPath.resolve(position + ".parquet");
			writeParquetFile(path, schema, currentRecord);
		}

		// Create a SUCCESS file, to act like some parquet writers
		Files.createFile(folderPath.resolve("_SUCCESS"));
	}

	/**
	 * Creates a parquet parser builder on this instance's datastore.
	 *
	 * @param doBatching whether or not to do batching
	 * @return the builder
	 */
	protected static IParquetParserBuilder createBasicParserBuilder(
			final IDatastore datastore,
			final boolean doBatching) {
		final IParquetParserBuilder builder = new ParquetParserBuilder(datastore);

		if (!doBatching) {
			builder.nonBatched();
		}

		return builder;
	}

	protected static long parseFile(
			final IDatastore datastore,
			final IParquetParser parquetParser,
			final String filepath,
			final String storename,
			final Map<String, String> mapping)
			throws NoTransactionException {
		INamedStoreToParquetMappingBuilder builder = IStoreToParquetMapping.builder().onStore(storename);
		for (final Entry<String, String> entry : mapping.entrySet()) {
			builder = builder.feedStoreField(entry.getKey()).withParquetField(entry.getValue());
		}
		return parseFile(datastore, parquetParser, filepath, builder.build());
	}

	protected static long parseFile(
			final IDatastore datastore,
			final IParquetParser parquetParser,
			final String filepath,
			final IStoreToParquetMapping... mappings)
			throws NoTransactionException {
		return parseFile(datastore, parquetParser, filepath, null, mappings);
	}

	protected static long parseFile(
			final IDatastore datastore,
			final IParquetParser parquetParser,
			final String filepath,
			final IParquetFieldParsers parsers,
			final IStoreToParquetMapping... mappings) {
		final long timeBefore = System.nanoTime();
		datastore.edit(t -> {
			try {
				parquetParser.parse(
						new org.apache.hadoop.fs.Path(filepath),
						parsers,
						mappings[0],
						Arrays.stream(mappings)
								.filter(m -> m != mappings[0])
								.toArray(IStoreToParquetMapping[]::new));
				t.forceCommit();
			} catch (NoTransactionException | IOException e) {
				throw new ActiveViamRuntimeException(e);
			}
		});

		return System.nanoTime() - timeBefore;
	}

	protected static void assertValuesOfFirstField(
			final IDatastore datastore,
			final String storeName,
			final int expectedSize,
			final ThrowingBiConsumer<Integer, Object> check) {
		final IDictionaryCursor cursor = datastore.getHead()
				.getQueryRunner()
				.forStore(storeName)
				.withCondition(BaseConditions.TRUE)
				.selectingAllStoreFields()
				.run();

		int i = 0;
		while (cursor.next()) {
			check.accept(i, cursor.getRecord().read(0));
			i++;
		}
		Assertions.assertEquals(expectedSize, i);
	}

	public static void assertValuesOfFirstField(
			final IDatastore datastore,
			final String storeName,
			final double[][] values) {
		SoftAssertions.assertSoftly(
				assertions -> assertValuesOfFirstField(
						datastore,
						storeName,
						values.length,
						(i, value) -> assertions.assertThat(((IVector) value).toDoubleArray())
								.as("row " + i)
								.containsExactly(values[i])));
	}

	public static Type literalTypeToAvroType(String literalType) {
		switch (literalType) {
			case ILiteralType.BOOLEAN:
				return Type.BOOLEAN;
			case ILiteralType.INT:
				return Type.INT;
			case ILiteralType.LONG:
				return Type.LONG;
			case ILiteralType.FLOAT:
				return Type.FLOAT;
			case ILiteralType.DOUBLE:
				return Type.DOUBLE;
			case ILiteralType.STRING:
				return Type.STRING;
			default:
				throw new IllegalArgumentException("unsupported type " + literalType);
		}
	}

}
