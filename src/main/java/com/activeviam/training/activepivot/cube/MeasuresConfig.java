package com.activeviam.training.activepivot.cube;

import com.activeviam.copper.api.Copper;
import com.activeviam.copper.api.CopperMeasure;
import com.activeviam.copper.api.CopperStore;
import com.activeviam.copper.api.MultiLevelHierarchyBuilder;
import com.activeviam.training.activepivot.PivotManagerConfig;
import com.activeviam.training.constants.StoreAndFieldConstants;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MeasuresConfig {


    @Bean
    public CopperMeasure notional() {
        return Copper.sum(StoreAndFieldConstants.TRADES_NOTIONAL)
                .as(StoreAndFieldConstants.TRADES_NOTIONAL)
                .withFormatter(PivotManagerConfig.DOUBLE_FORMATTER);
    }

}
