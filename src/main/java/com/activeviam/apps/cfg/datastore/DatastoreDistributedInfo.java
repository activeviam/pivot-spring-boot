/*
 * Copyright (C) ActiveViam 2023
 * ALL RIGHTS RESERVED. This material is the CONFIDENTIAL and PROPRIETARY
 * property of ActiveViam Limited. Any unauthorized use,
 * reproduction or transfer of this material is strictly prohibited
 */

package com.activeviam.apps.cfg.datastore;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.activeviam.apps.cfg.IDistributedInfo;

@Configuration
@Profile("datastore")
public class DatastoreDistributedInfo implements IDistributedInfo {


    @Override
    public String getCubeName() {
        return "datastore-cube";
    }

    @Override
    public boolean isDatacube() {
        return true;
    }

}
