/*
 * Copyright (C) ActiveViam 2024
 * ALL RIGHTS RESERVED. This material is the CONFIDENTIAL and PROPRIETARY
 * property of ActiveViam Limited. Any unauthorized use,
 * reproduction or transfer of this material is strictly prohibited
 */

package com.activeviam.apps.cfg.pivot;

import static com.activeviam.apps.constants.StoreAndFieldConstants.TRADES_NOTIONAL;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.activeviam.copper.api.Copper;
import com.activeviam.cube.configurators.CalculationsConfigurator;
import com.activeviam.cube.configurators.DecoratedMeasuresChain;
import com.activeviam.cube.configurators.MeasuresChain;
import com.activeviam.cube.configurators.impl.AtotiConfigurationUtils;
import com.activeviam.cube.decorators.MeasureDecorator;
import com.activeviam.cube.decorators.NameMappedMeasuresDecorator;
import com.activeviam.cube.properties.FormatterProperties;

import lombok.RequiredArgsConstructor;

@Configuration
@Import({FormatterProperties.class, NameMappedMeasuresDecorator.class})
@RequiredArgsConstructor
public class CalculationsConfiguration {

    public static final String NATIVE_FOLDER = "Native measures";

    @Bean
    MeasuresChain nativeCalculations(MeasureDecorator decorator) {
        return AtotiConfigurationUtils.measuresChain(
                decorator,
                Copper.count().withinFolder(NATIVE_FOLDER),
                Copper.timestamp().withAlias("Update.Timestamp").withinFolder(NATIVE_FOLDER),
                Copper.sum(TRADES_NOTIONAL).as(TRADES_NOTIONAL));
    }

    @Bean
    MeasuresChain tradeCalculations(MeasureDecorator decorator) {
        return new TradesCalculations(decorator);
    }

    @Bean
    CalculationsConfigurator calculations(List<MeasuresChain> measureChains) {
        return AtotiConfigurationUtils.cubeCalculationsConfigurator()
                .withCalculations(measureChains)
                .build();
    }

    private static class TradesCalculations extends DecoratedMeasuresChain {

        public TradesCalculations(MeasureDecorator decorator) {
            super(decorator);
            add(Copper.sum(TRADES_NOTIONAL).as(TRADES_NOTIONAL));
        }
    }
}
