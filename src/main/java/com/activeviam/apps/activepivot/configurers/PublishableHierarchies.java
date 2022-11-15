package com.activeviam.apps.activepivot.configurers;

import com.activeviam.copper.api.CopperHierarchy;
import com.activeviam.copper.api.Publishable;

import java.util.ArrayList;
import java.util.Collection;

public class PublishableHierarchies implements PublishableCollection<CopperHierarchy> {
	private final Collection<Publishable<CopperHierarchy>> collection = new ArrayList<>();

	@Override
	public Collection<Publishable<CopperHierarchy>> getCollection() {
		return collection;
	}
}
