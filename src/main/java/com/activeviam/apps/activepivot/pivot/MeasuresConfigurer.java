package com.activeviam.apps.activepivot.pivot;

import static com.activeviam.apps.activepivot.data.datastore.StoreAndFieldConstants.TRADES_NOTIONAL;
import static com.activeviam.apps.activepivot.pivot.CubeConstants.*;

import com.activeviam.copper.ICopperContext;
import com.activeviam.copper.api.Copper;
import org.springframework.stereotype.Component;

@Component
public class MeasuresConfigurer {
    public void add(final ICopperContext context) {

        Copper.count()
                .withAlias("Count")
                .withinFolder(NATIVE_MEASURES)
                .withFormatter(INT_FORMATTER)
                .publish(context);

        Copper.timestamp().withinFolder(NATIVE_MEASURES).publish(context);

        Copper.sum(TRADES_NOTIONAL)
                .as(TRADES_NOTIONAL)
                .withFormatter(DOUBLE_FORMATTER)
                .publish(context);
    }
}
