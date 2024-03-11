/*
 * (C) ActiveViam 2024
 * ALL RIGHTS RESERVED. This material is the CONFIDENTIAL and PROPRIETARY
 * property of ActiveViam. Any unauthorized use,
 * reproduction or transfer of this material is strictly prohibited
 */

package com.activeviam.parquet;

import com.activeviam.fwk.ActiveViamRuntimeException;
import com.activeviam.parquet.impl.ParquetParserBuilder;
import com.qfs.desc.impl.DatastoreSchemaDescriptionBuilder;
import com.qfs.desc.impl.FieldDescription;
import com.qfs.desc.impl.StoreDescription;
import com.qfs.literal.ILiteralType;
import com.qfs.store.IDatastore;
import com.qfs.store.build.impl.DatastoreBuilder;
import com.qfs.store.transaction.DatastoreTransactionException;
import com.qfs.store.transaction.ITransactionManager;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.apache.avro.generic.GenericData.Record;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.RepeatedTest;

import static com.activeviam.parquet.ParquetTestUtils.writeParquetFile;

public class TestConcurrencyIssue {

	private static final String STORE = "store";

	private static final List<String> fields = List.of("a", "b", "c", "d", "e");

	private static final int NUMBER_OF_FILE = 20;

	private static final int FILE_SIZE = 100000;

	private static final int REAPEAT = 1000;

	public static final Path PARQUET_DATA_FOLDER =
			FileSystems.getDefault().getPath("src", "test", "resources", "data");
	public static final Path PARQUET_SAVE_FOLDER =
			FileSystems.getDefault().getPath("src", "test", "resources", "save");

	public static final Schema schema = SchemaBuilder.record("simpleSchema")
			.fields()
			.name("a")
			.type()
			.stringType()
			.noDefault()
			.name("b")
			.type()
			.stringType()
			.noDefault()
			.name("c")
			.type()
			.stringType()
			.noDefault()
			.name("d")
			.type()
			.stringType()
			.noDefault()
			.name("e")
			.type()
			.stringType()
			.noDefault()
			.endRecord();

	@BeforeAll
	public static void generateFiles() {
		IntStream.range(0, NUMBER_OF_FILE).mapToObj(i -> PARQUET_DATA_FOLDER.resolve("file_"+ i + ".parquet"))
				.map(Path::toString)
				.parallel()
				.forEach(TestConcurrencyIssue::generateFile);
	}

	public static void generateFile(final String filePath) {
		final Collection<Record> recordsToWrite =
				IntStream.range(0, FILE_SIZE).mapToObj(i -> {
					final Record record = new Record(schema);
					for(final String field : fields) {
						record.put(field, "field_" + field + "_" + i);
					}
					return record;
				}).collect(Collectors.toList());

		try {
			writeParquetFile(filePath, schema, recordsToWrite);
		} catch (IOException | InterruptedException e) {
			throw new ActiveViamRuntimeException("Failed to generate file " + filePath, e);
		}
		System.out.println("Generated file " + filePath);
	}

	@RepeatedTest(50)
	public void test() throws Exception {

		final var store =
				new StoreDescription(
						STORE,
						Collections.emptyList(),
						fields.stream().map(f -> FieldDescription.builder().name(f).dataType(ILiteralType.STRING).build()).collect(
								Collectors.toList()));
		final IDatastore datastore = new DatastoreBuilder()
				.setSchemaDescription(new DatastoreSchemaDescriptionBuilder().withStore(store).build())
				.build();
		final IStoreToParquetMapping mapping = IStoreToParquetMapping.builder()
				.onStore(STORE)
				.build();

		datastore.edit(tm -> tm.add(STORE, fields.toArray()));

		final ITransactionManager tm = datastore.getTransactionManager();

		Files.copy(
				PARQUET_SAVE_FOLDER.resolve("file_0.parquet"),
				PARQUET_DATA_FOLDER.resolve("file_0.parquet"),
				StandardCopyOption.REPLACE_EXISTING);
		Files.copy(PARQUET_DATA_FOLDER.resolve("file_0.parquet"),
				PARQUET_SAVE_FOLDER.resolve("file_0.parquet"),
				StandardCopyOption.REPLACE_EXISTING);


		for (int i =0; i<REAPEAT; i++) {
			final IParquetParser parser = new ParquetParserBuilder(datastore)
					.withNumberOfThreads(12)
					.build();
			Files.copy(
					PARQUET_SAVE_FOLDER.resolve("file_0.parquet"),
					PARQUET_DATA_FOLDER.resolve("file_0.parquet"),
					StandardCopyOption.REPLACE_EXISTING);

			try {
				tm.startTransaction(STORE);
				parser.parse(PARQUET_DATA_FOLDER.toString(), mapping); // fails because another thread deletes file_0
				tm.commitTransaction();
			} catch (ActiveViamRuntimeException e) {
//				parser.close();
				e.printStackTrace();
				try {
					tm.rollbackTransaction();
				} catch (DatastoreTransactionException e2) {
					throw new ActiveViamRuntimeException("Failed to rollback", e2);
				}
			}
//			parser.close();
		}

		tm.startTransaction(STORE); // this should fail
		tm.add(STORE, fields.toArray());
		tm.commitTransaction();

	}


}