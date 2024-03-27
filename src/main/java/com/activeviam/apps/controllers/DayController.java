/*
 * Copyright (C) ActiveViam 2023-2024
 * ALL RIGHTS RESERVED. This material is the CONFIDENTIAL and PROPRIETARY
 * property of ActiveViam Limited. Any unauthorized use,
 * reproduction or transfer of this material is strictly prohibited
 */
package com.activeviam.apps.controllers;

import static com.activeviam.apps.constants.StoreAndFieldConstants.ASOFDATE;
import static com.activeviam.apps.constants.StoreAndFieldConstants.TRADES_STORE_NAME;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.activeviam.config.AtotiService;

import lombok.RequiredArgsConstructor;

/**
 * This is an example of creating a custom REST services which queries the datastore
 * Although this is a simplistic example which returns how many days are loaded,
 * you can see how much easier and simpler this is compared to using traditional
 * REST services in ActivePivot
 */
@RestController
@RequiredArgsConstructor
public class DayController {
    private final AtotiService atotiService;

    @GetMapping("/daysLoaded")
    public long getNumberOfDays() {
        var query = atotiService
                .database()
                .getQueryManager()
                .distinctQuery()
                .forTable(TRADES_STORE_NAME)
                .withoutCondition()
                .withTableFields(ASOFDATE)
                .toQuery();
        return atotiService
                .database()
                .getMasterHead()
                .getQueryRunner()
                .distinctQuery(query)
                .run()
                .getRecord()
                .toList()
                .size();
    }
}
