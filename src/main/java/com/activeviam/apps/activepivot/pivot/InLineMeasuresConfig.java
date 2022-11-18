/*
 * (C) ActiveViam 2022
 * ALL RIGHTS RESERVED. This material is the CONFIDENTIAL and PROPRIETARY
 * property of ActiveViam. Any unauthorized use,
 * reproduction or transfer of this material is strictly prohibited
 */
package com.activeviam.apps.activepivot.pivot;

import com.activeviam.apps.activepivot.configurers.IMeasuresConfigurer;
import com.activeviam.apps.activepivot.configurers.annotation_multivalue.InCube;
import com.activeviam.apps.activepivot.configurers.annotation_repeatable.Cube;
import com.activeviam.copper.api.Copper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.activeviam.apps.activepivot.pivot.CubeConstants.*;

/**
 * @author ActiveViam
 */
@Configuration
public class InLineMeasuresConfig {

    @Bean("nativeMeasures")
    @Cube(TICKERS_CUBE_NAME)
    @Cube(TRADES_CUBE_NAME)
    IMeasuresConfigurer tickerMeasures() {
        return context -> {
            Copper.count()
                    .withAlias("Count")
                    .withinFolder(NATIVE_MEASURES)
                    .withFormatter(INT_FORMATTER)
                    .publish(context);
            Copper.timestamp().withinFolder(NATIVE_MEASURES).publish(context);
        };
    }
}
