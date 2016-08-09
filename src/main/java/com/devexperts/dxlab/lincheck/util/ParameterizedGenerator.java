package com.devexperts.dxlab.lincheck.util;

/**
 * Created by apykhtin on 8/8/2016.
 */
public interface ParameterizedGenerator extends Generator {
    public void setParameters(String... parameters);
}
