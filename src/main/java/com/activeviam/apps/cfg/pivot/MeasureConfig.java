/*
 * Copyright (C) ActiveViam 2023
 * ALL RIGHTS RESERVED. This material is the CONFIDENTIAL and PROPRIETARY
 * property of ActiveViam Limited. Any unauthorized use,
 * reproduction or transfer of this material is strictly prohibited
 */
package com.activeviam.apps.cfg.pivot;

import static com.activeviam.apps.cfg.pivot.PivotManagerConfig.DOUBLE_FORMATTER;
import static com.activeviam.apps.constants.StoreAndFieldConstants.TRADE_PNL_MTM;

import org.springframework.context.annotation.Configuration;

import com.activeviam.copper.ICopperContext;
import com.activeviam.copper.api.Copper;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Configuration
public class MeasureConfig {

    public void build(ICopperContext context) {
        Copper.sum(TRADE_PNL_MTM)
                .as(TRADE_PNL_MTM)
                .withFormatter(DOUBLE_FORMATTER)
                .publish(context);
//        Copper.sum(TRADE_PNL_PNL)
//                .as(TRADE_PNL_PNL)
//                .withFormatter(DOUBLE_FORMATTER)
//                .publish(context);
    }
}
