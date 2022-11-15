package com.activeviam.apps.activepivot.configurers;

import com.activeviam.copper.ICopperContext;
import org.springframework.stereotype.Component;

@Component
public interface ICalculationsConfigurer {

    default PublishableHierarchies getPublishableHierarchies() {
        return new PublishableHierarchies();
    }

    PublishableMeasures getPublishableMeasures();

    default void publish(ICopperContext context) {
        getPublishableHierarchies().forEach(p -> p.publish(context));
        getPublishableMeasures().forEach(p -> p.publish(context));
    }
}
