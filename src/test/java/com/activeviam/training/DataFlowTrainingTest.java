package com.activeviam.training;


import com.activeviam.training.activepivot.source.SourceConfig;
import com.qfs.store.IDatastore;
import com.qfs.store.query.impl.DatastoreQueryHelper;
import com.quartetfs.biz.pivot.IActivePivotManager;
import com.quartetfs.biz.pivot.IActivePivotVersion;
import com.quartetfs.biz.pivot.ILocation;
import com.quartetfs.biz.pivot.cube.hierarchy.impl.HierarchiesUtil;
import com.quartetfs.biz.pivot.impl.LocationUtil;
import com.quartetfs.biz.pivot.query.impl.GetAggregatesQuery;
import com.quartetfs.biz.pivot.webservices.IQueriesService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.web.WebAppConfiguration;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static com.activeviam.training.activepivot.PivotManagerConfig.CUBE_NAME;
import static com.activeviam.training.constants.StoreAndFieldConstants.*;

@SpringBootTest(classes = PivotApplication.class)
@WebAppConfiguration
class DataFlowTrainingTest {

    @Autowired
    private IDatastore datastore;
    @Autowired
    private IQueriesService queriesService;
    @Autowired
    private IActivePivotManager manager;


    // ================================
    // TODO Task 1: Inserting data
    // ================================

    /**
     * Insert a row into the datastore
     */
    @Test
    void insertDirectlyToDatastore() {
        datastore.getHead();
    }


    /**
     * Insert an invalid row into the datastore
     */
    @Test
    void insertInvalidRowToDatastore() {
        datastore.getHead();
    }

    /**
     * Use what we've configured in {@link SourceConfig}
     */
    @Test
    void fetchViaSource() {
    }


    // ================================
    // TODO Task 2: Querying datastore
    // ================================


    /**
     * Query TRADES store for all records, with a result limit of 1000
     */
    @Test
    void queryTradesWithResultLimit() {

    }

    /**
     * Query the TRADES store for all records for date YYYY-mm-DD
     * HINT: See {@link com.qfs.condition.impl.BaseConditions}
     */
    @Test
    void queryTradesForDate() {
    }

    /**
     * Query the INSTRUMENT store for all trades found in TRADES for date YYYY-mm-DD
     * How can we chain the datastore queries?
     */
    @Test
    void queryInstrumentsWithTradesResult() {
    }


    /**
     * Create a GetByKey query on store INSTRUMENT
     * Hint: What static helpers do we have available?
     */
    @Test
    void queryDatastoreByKey() {
    }


    // ================================
    // TODO Task 3: Querying the cube
    // ================================


    /**
     * Query the cube using the IQueriesService for the total Notional.SUM
     * Q: What type of query object can we use for this?
     */
    @Test
    void queryCubeForNotionalSum() {

    }


    /**
     * Query the cube using the IQueriesService for the total Notional.SUM
     * for a specific date
     */
    @Test
    void queryCubeWithIQuery() {

        IActivePivotVersion cube = manager.getActivePivots().get(CUBE_NAME).getHead();

        // Get hierarchy to expand


        // Create a location for us to modify
        final ILocation location = cube.getLocationConverter()
                // TODO Check what boolean refers to here
                .computeLocation(Map.of(ASOFDATE, LocalDate.of(2022, 2, 1)), true);
        
        final GetAggregatesQuery query = new GetAggregatesQuery();
        query.setLocations(List.of(location));
        
        queriesService.execute(query);

    }

    /**
     * Find children of member ALLMEMBER in hierarchy Trades
     * HINT: See {@link com.qfs.condition.impl.BaseConditions}
     */
    @Test
    void getChildrenOfHierarchy() {
    }
}
