package com.devexperts.dxlab.lincheck.generators;


import com.devexperts.dxlab.lincheck.util.ParameterizedGenerator;

public class IntegerGenerator implements ParameterizedGenerator {
    private int begin = 0; // TODO bug, never used
    private int end = 10;

    public IntegerGenerator() {
    }

    // TODO generate only one value
    public Integer[] generate() {
        Integer[] x = new Integer[end];
        for (int i = 0; i < end; i++) {
            x[i] = i;
        }
        return x;
    }

    public void setParameters(String... parameters) {
        this.begin = Integer.parseInt(parameters[0]);
        this.end = Integer.parseInt(parameters[1]);
    }
}
