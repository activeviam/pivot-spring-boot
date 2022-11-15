package com.activeviam.apps.activepivot.pivot.configurers;

import com.activeviam.copper.ICopperContext;
import org.springframework.stereotype.Component;

@Component
public interface IMeasuresConfigurer {
	void build(final ICopperContext context);
}
