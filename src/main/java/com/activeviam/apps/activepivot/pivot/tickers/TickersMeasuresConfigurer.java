package com.activeviam.apps.activepivot.pivot.tickers;

import com.activeviam.apps.activepivot.configurers.IMeasuresConfigurer;
import com.activeviam.apps.activepivot.configurers.annotation_multivalue.InCube;
import com.activeviam.apps.activepivot.configurers.annotation_repeatable.Cube;
import com.activeviam.copper.ICopperContext;
import com.activeviam.copper.api.Copper;
import org.springframework.stereotype.Component;

import static com.activeviam.apps.activepivot.pivot.CubeConstants.*;

@Component("classConfigurer")
@Cube(TICKERS_CUBE_NAME)
public class TickersMeasuresConfigurer implements IMeasuresConfigurer {
    public void add(final ICopperContext context) {
        Copper.count().multiply(Copper.constant(2)).withName("Double count").publish(context);
    }
}
