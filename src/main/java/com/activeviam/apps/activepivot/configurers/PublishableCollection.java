package com.activeviam.apps.activepivot.configurers;

import com.activeviam.copper.api.Publishable;

import java.util.Collection;
import java.util.function.Consumer;

public interface PublishableCollection<T> {

    Collection<Publishable<T>> getCollection();

    default void forEach(Consumer<Publishable<T>> action) {
        getCollection().forEach(action);
    }

    default void add(Publishable<T> publishable) {
        getCollection().add(publishable);
    }

}
