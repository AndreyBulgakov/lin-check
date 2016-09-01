package com.devexperts.dxlab.lincheck.util;

import com.devexperts.dxlab.lincheck.generators.Generator;

/**
 * TODO delete this class
 */
public interface ParameterizedGenerator extends Generator {
    public void setParameters(String... parameters);
}
