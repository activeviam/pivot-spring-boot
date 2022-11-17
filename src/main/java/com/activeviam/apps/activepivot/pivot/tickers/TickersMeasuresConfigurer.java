package com.activeviam.apps.activepivot.pivot.tickers;

import com.activeviam.apps.activepivot.configurers.IMeasuresConfigurer;
import com.activeviam.apps.activepivot.configurers.annotation.InCube;
import com.activeviam.copper.ICopperContext;
import com.activeviam.copper.api.Copper;
import org.springframework.stereotype.Component;

import static com.activeviam.apps.activepivot.pivot.CubeConstants.*;

@Component("classConfigurer")
@InCube(TICKERS_CUBE_NAME)
public class TickersMeasuresConfigurer implements IMeasuresConfigurer {
    public void add(final ICopperContext context) {

        Copper.count()
                .withAlias("Count")
                .withinFolder(NATIVE_MEASURES)
                .withFormatter(INT_FORMATTER)
                .publish(context);

        Copper.timestamp().withinFolder(NATIVE_MEASURES).publish(context);
    }
}
