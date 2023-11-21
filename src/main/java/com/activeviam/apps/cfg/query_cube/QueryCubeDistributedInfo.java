/*
 * Copyright (C) ActiveViam 2023
 * ALL RIGHTS RESERVED. This material is the CONFIDENTIAL and PROPRIETARY
 * property of ActiveViam Limited. Any unauthorized use,
 * reproduction or transfer of this material is strictly prohibited
 */

package com.activeviam.apps.cfg.query_cube;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.activeviam.apps.cfg.IDistributedInfo;

@Configuration
@Profile("query")
public class QueryCubeDistributedInfo implements IDistributedInfo {

    @Override
    public String getCubeName() {
        return "query-cube";
    }

    @Override
    public boolean isDatacube() {
        return false;
    }
}
