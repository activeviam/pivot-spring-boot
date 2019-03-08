package com.sbr.pivotspringboot.activepivot;

import com.qfs.store.IDatastore;
import com.qfs.store.query.impl.DatastoreQueryHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.sbr.pivotspringboot.activepivot.StoreAndFieldConstants.ASOFDATE;
import static com.sbr.pivotspringboot.activepivot.StoreAndFieldConstants.TRADES_STORE_NAME;

/**
 * This is an example of creating a custom REST services which queries the datastore
 * Although this is a simplistic example which returns how many days are loaded,
 * you can see how much easier and simpler this is compared to using traditional
 * REST services in ActivePivot
 */
@RestController
public class DayController {

    @Autowired
    IDatastore datastore;

    @RequestMapping("/daysLoaded")
    public long getNumberOfDays() {
        return DatastoreQueryHelper.selectDistinct(datastore.getMostRecentVersion(), TRADES_STORE_NAME, ASOFDATE).size();
    }
}
