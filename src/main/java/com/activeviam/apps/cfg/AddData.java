package com.activeviam.apps.cfg;

import com.activeviam.apps.constants.StoreAndFieldConstants;
import com.qfs.store.IDatastore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AddData {

    @Autowired
    IDatastore datastore;


    @Bean
    public Void addData() {
        datastore.edit(t -> {

            // Add positions data
            t.add(StoreAndFieldConstants.POSITIONS_STORE_NAME,1,1,"A","2B", "3BC", 45.35);
            t.add(StoreAndFieldConstants.POSITIONS_STORE_NAME,2,1,"A","2B", "3BD", 79.81);
            t.add(StoreAndFieldConstants.POSITIONS_STORE_NAME,3,1,"A","2C", "3AC", 42.42);
            t.add(StoreAndFieldConstants.POSITIONS_STORE_NAME,4,2,"A","2C", "3BC", 99.19);
            t.add(StoreAndFieldConstants.POSITIONS_STORE_NAME,5,2,"A","2C", "3CC", 25.83);

            // Add instruments data
            t.add(StoreAndFieldConstants.INSTRUMENTS_STORE_NAME, 1, 100d);
            t.add(StoreAndFieldConstants.INSTRUMENTS_STORE_NAME, 2, 50d);
        });

        return null;
    }

}
