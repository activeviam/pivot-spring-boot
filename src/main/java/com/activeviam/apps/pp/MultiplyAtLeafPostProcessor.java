package com.activeviam.apps.pp;

import com.quartetfs.biz.pivot.ILocation;
import com.quartetfs.biz.pivot.cube.hierarchy.measures.IPostProcessorCreationContext;
import com.quartetfs.biz.pivot.postprocessing.IPostProcessor;
import com.quartetfs.biz.pivot.postprocessing.impl.ADynamicAggregationPostProcessor;
import com.quartetfs.fwk.QuartetExtendedPluginValue;

@QuartetExtendedPluginValue(intf = IPostProcessor.class, key = MultiplyAtLeafPostProcessor.PLUGIN_KEY)
public class MultiplyAtLeafPostProcessor extends ADynamicAggregationPostProcessor<Double, Double> {

    public static final String PLUGIN_KEY = "MULTIPLY_AT_LEAF";

    public MultiplyAtLeafPostProcessor(String name, IPostProcessorCreationContext creationContext) {
        super(name, creationContext);
    }

    @Override
    protected Double evaluateLeaf(ILocation leafLocation, Object[] underlyingMeasures) {
        if (underlyingMeasures[0] == null || underlyingMeasures[1] == null) {
            return null;
        }
        return (double) underlyingMeasures[0] * (double) underlyingMeasures[1];
    }

    @Override
    public String getType() {
        return PLUGIN_KEY;
    }
}
