/*
 * (C) ActiveViam 2022
 * ALL RIGHTS RESERVED. This material is the CONFIDENTIAL and PROPRIETARY
 * property of ActiveViam. Any unauthorized use,
 * reproduction or transfer of this material is strictly prohibited
 */
package com.activeviam.apps.activepivot.pivot.tickers;

import com.activeviam.apps.activepivot.configurers.IDimensionsConfigurer;
import com.activeviam.apps.activepivot.configurers.annotation.InCube;
import com.activeviam.desc.build.ICanBuildCubeDescription;
import com.activeviam.desc.build.dimensions.ICanStartBuildingDimensions;
import com.quartetfs.biz.pivot.definitions.IActivePivotInstanceDescription;
import org.springframework.stereotype.Component;

import static com.activeviam.apps.activepivot.pivot.CubeConstants.*;

/**
 * @author ActiveViam
 */
@Component
@InCube(TICKERS_CUBE_NAME)
public class TickersDimensionsConfigurer implements IDimensionsConfigurer {
    @Override
    public ICanBuildCubeDescription<IActivePivotInstanceDescription> add(ICanStartBuildingDimensions builder) {
        return builder.withSingleLevelDimensions(TRADE_ID)
                .withSingleLevelDimension(TRADE_TICKER);
    }
}
