/*
 * Copyright (C) ActiveViam 2023
 * ALL RIGHTS RESERVED. This material is the CONFIDENTIAL and PROPRIETARY
 * property of ActiveViam Limited. Any unauthorized use,
 * reproduction or transfer of this material is strictly prohibited
 */
package com.activeviam.apps.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.activeviam.apps.constants.StoreAndFieldConstants;
import com.qfs.store.IDatastore;
import com.qfs.store.query.impl.DatastoreQueryHelper;

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
    private final IDatastore datastore;

    @GetMapping("/daysLoaded")
    public long getNumberOfDays() {
        return DatastoreQueryHelper.selectDistinct(
                        datastore.getMostRecentVersion(),
                        StoreAndFieldConstants.TRADES_STORE_NAME,
                        StoreAndFieldConstants.ASOFDATE)
                .size();
    }
}
