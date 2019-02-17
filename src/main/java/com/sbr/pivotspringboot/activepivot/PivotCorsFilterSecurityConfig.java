package com.sbr.pivotspringboot.activepivot;

import com.qfs.security.cfg.impl.ACorsFilterConfig;

import java.util.Arrays;
import java.util.Collection;

public class PivotCorsFilterSecurityConfig extends ACorsFilterConfig {

    @Override
    public Collection<String> getAllowedOrigins() {
        return Arrays.asList();
    }
}
