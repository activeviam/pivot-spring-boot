package com.activeviam.apps.activepivot.configurers;

import com.activeviam.copper.api.CopperMeasure;
import com.activeviam.copper.api.Publishable;

import java.util.ArrayList;
import java.util.Collection;

public class PublishableMeasures implements PublishableCollection<CopperMeasure> {
	private final Collection<Publishable<CopperMeasure>> collection = new ArrayList<>();

	@Override
	public Collection<Publishable<CopperMeasure>> getCollection() {
		return collection;
	}
}
