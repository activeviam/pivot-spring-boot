package com.activeviam.apps.activepivot.pivot;

import static com.activeviam.apps.activepivot.data.datastore.StoreAndFieldConstants.TRADES_NOTIONAL;
import static com.activeviam.apps.activepivot.pivot.CubeConstants.*;

import com.activeviam.apps.activepivot.configurers.ICalculationsConfigurer;
import com.activeviam.apps.activepivot.configurers.PublishableHierarchies;
import com.activeviam.apps.activepivot.configurers.PublishableMeasures;
import com.activeviam.copper.api.Copper;
import org.springframework.stereotype.Component;

@Component
public class CalculationsConfigurer implements ICalculationsConfigurer {
    @Override
    public PublishableHierarchies getPublishableHierarchies() {
        return ICalculationsConfigurer.super.getPublishableHierarchies();
    }

    @Override
    public PublishableMeasures getPublishableMeasures() {
        var measures = new PublishableMeasures();
        measures.add(
                Copper.count()
                .withAlias("Count")
                .withinFolder(NATIVE_MEASURES)
                .withFormatter(INT_FORMATTER)
        );
        measures.add(Copper.timestamp().withinFolder(NATIVE_MEASURES));
        measures.add(Copper.sum(TRADES_NOTIONAL)
                .as(TRADES_NOTIONAL)
                .withFormatter(DOUBLE_FORMATTER));
        return measures;
    }

}
