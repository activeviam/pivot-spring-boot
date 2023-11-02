package com.activeviam.apps.cfg;

import com.activeviam.apps.controllers.TradeGeneratorController;
import com.qfs.store.IDatastore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TradeGeneratorConfiguration {

    @Bean
    public TradeGeneratorController tradeGeneratorController(IDatastore datastore){
        return new TradeGeneratorController(datastore);
    }
}
