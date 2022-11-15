/*
 * (C) ActiveViam 2022
 * ALL RIGHTS RESERVED. This material is the CONFIDENTIAL and PROPRIETARY
 * property of ActiveViam. Any unauthorized use,
 * reproduction or transfer of this material is strictly prohibited
 */
package com.activeviam.apps.activepivot.data.source;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

/**
 * @author ActiveViam
 */
@Configuration
public class InitialLoadConfiguration {
    private final DataLoadingService dataLoadingService;

    public InitialLoadConfiguration(DataLoadingService dataLoadingService) {
        this.dataLoadingService = dataLoadingService;
    }

    /*
     * **************************** Initial load *********************************
     */

    @EventListener(ApplicationReadyEvent.class)
    public void initialLoad() {
        dataLoadingService.loadData();
    }
}
