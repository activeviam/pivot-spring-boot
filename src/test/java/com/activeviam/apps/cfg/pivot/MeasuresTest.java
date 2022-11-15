package com.activeviam.apps.cfg.pivot;

import com.activeviam.apps.activepivot.configurers.IDatastoreConfigurer;
import com.activeviam.apps.activepivot.data.datastore.DatastoreConfigurer;
import com.activeviam.apps.activepivot.pivot.DimensionsConfigurer;
import com.activeviam.apps.activepivot.pivot.MeasuresConfigurer;
import com.activeviam.apps.activepivot.pivot.SchemaSelectionConfigurer;
import com.activeviam.apps.activepivot.configurers.IDimensionsConfigurer;
import com.activeviam.apps.activepivot.configurers.IMeasuresConfigurer;
import com.activeviam.apps.activepivot.configurers.ISchemaSelectionConfigurer;
import com.activeviam.builders.StartBuilding;
import com.activeviam.copper.CopperRegistrations;
import com.activeviam.copper.builders.ITransactionsBuilder;
import com.activeviam.copper.builders.impl.SimpleTransactionBuilder;
import com.activeviam.copper.testing.CubeTester;
import com.activeviam.copper.testing.CubeTesterBuilder;
import com.activeviam.copper.testing.CubeTesterBuilderExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.time.LocalDate;

import static com.activeviam.apps.activepivot.data.datastore.StoreAndFieldConstants.TRADES_NOTIONAL;
import static com.activeviam.apps.activepivot.data.datastore.StoreAndFieldConstants.TRADES_STORE_NAME;
import static com.activeviam.apps.activepivot.pivot.CubeConstants.CUBE_NAME;

@SpringJUnitConfig
class MeasuresTest {

    /**
     * 	 NOTE: in this test, we setup the cube and load the data once. We then create a Tester as a bean and reuse it
     * 	 for each test.
     * 	 Also note we are not registering the CubeTesterBuilderExtension as an actual Extension (using @RegisterExtension)
     * 	 because we don't want it to create and destroy the test cube before each test
     * 	 (see {@link CubeTesterBuilderExtension#beforeEach(ExtensionContext)}
     * 	 and {@link CubeTesterBuilderExtension#afterEach(ExtensionContext)})
     *
     * 	 If for some reason we need to recreate the cube before each test (because e.g. the data has been modified in
     * 	 the test), then it is better to use the approach in {@link MeasuresTestAlternative}
     */
    @TestConfiguration
    @Import(value = {DatastoreConfigurer.class, SchemaSelectionConfigurer.class, DimensionsConfigurer.class, MeasuresConfigurer.class})
    public static class MeasuresTestConfiguration {

		// Add customer plugins (e.g. PostProcessors etc) here if needed!
		static {
			CopperRegistrations.setupRegistryForTests();
		}


		/**
		 * Creates the tester using the descriptions of the project.
		 *
		 * @return The tester.
		 */
		@Bean
		CubeTesterBuilder testerBuilder(
				IDatastoreConfigurer datastoreConfigurer,
				ISchemaSelectionConfigurer selectionConfigurer,
				IDimensionsConfigurer dimensionsConfigurer) {
			final var datastoreDescription = datastoreConfigurer.schemaDescription();
			final var selectionDescription = selectionConfigurer.createSchemaSelectionDescription(datastoreDescription);
			final var cubeDescription = StartBuilding.cube()
					.withName(CUBE_NAME)
					.withDimensions(dimensionsConfigurer::add)
					.build();
			return new CubeTesterBuilder(
					datastoreDescription,
					selectionDescription,
					cubeDescription);
		}

		@Bean
		public CubeTesterBuilderExtension cubeTesterBuilderExtension(CubeTesterBuilder cubeTesterBuilder){
			return new CubeTesterBuilderExtension(cubeTesterBuilder);
		}

		@Bean
		public CubeTester createTester(CubeTesterBuilderExtension cubeTesterBuilderExtension, IMeasuresConfigurer measures) {
			return cubeTesterBuilderExtension
					.setData(createTestData())
					.build(measures::add);
		}

		private static ITransactionsBuilder createTestData() {
			return SimpleTransactionBuilder.start()
					.inStore(TRADES_STORE_NAME)
					.add(LocalDate.parse("2019-03-13"), "T1", 100d)
					.add(LocalDate.parse("2019-03-13"), "T2", 350d)
					.add(LocalDate.parse("2019-03-13"), "T3", 300d)
					.end();

		}
	}
	@Autowired
	CubeTester tester;

	/**
	 * Here is the actual test. Check that the numbers sum up correctly
	 */
	@Test
	void tradesNotionalTotal_test() {
		// We need to inject the
		tester.query()
				.forMeasures(TRADES_NOTIONAL)
				.run()
				.getTester()
				.hasOnlyOneCellWith()
				.containing(750d);
	}

	/**
	 * Run a test on an MDX query with a slicer
	 */
	@Test
	void tradesNotionalTotal_withSlicer_test() {
		tester.mdxQuery("SELECT" +
						"  [Measures].[Notional] ON COLUMNS" +
						"  FROM [Cube]" +
						"  WHERE [TradeID].[TradeID].[ALL].[AllMember].[T1]")
				.getTester()
				.hasOnlyOneCell()
				.containingFormattedValue("100");
	}

}
