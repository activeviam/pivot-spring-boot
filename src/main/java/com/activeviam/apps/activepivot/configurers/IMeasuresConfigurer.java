package com.activeviam.apps.activepivot.configurers;

import com.activeviam.copper.ICopperContext;
import org.springframework.stereotype.Component;

@Component
public interface IMeasuresConfigurer {
	void add(final ICopperContext context);
}
