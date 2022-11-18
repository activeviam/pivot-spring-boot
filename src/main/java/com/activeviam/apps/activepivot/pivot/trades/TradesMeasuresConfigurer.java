package com.activeviam.apps.activepivot.pivot.trades;

import com.activeviam.apps.activepivot.configurers.IMeasuresConfigurer;
import com.activeviam.apps.activepivot.configurers.annotation_multivalue.InCube;
import com.activeviam.apps.activepivot.configurers.annotation_repeatable.Cube;
import com.activeviam.copper.ICopperContext;
import com.activeviam.copper.api.Copper;
import org.springframework.stereotype.Component;

import static com.activeviam.apps.activepivot.data.datastore.StoreAndFieldConstants.TRADES_NOTIONAL;
import static com.activeviam.apps.activepivot.pivot.CubeConstants.*;

@Component
@Cube(TRADES_CUBE_NAME)
public class TradesMeasuresConfigurer implements IMeasuresConfigurer {
    public void add(final ICopperContext context) {

        Copper.sum(TRADES_NOTIONAL)
                .as(TRADES_NOTIONAL)
                .withFormatter(DOUBLE_FORMATTER)
                .publish(context);
    }
}
