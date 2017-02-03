package com.devexperts.dxlab.lincheck.generators;

import com.devexperts.dxlab.lincheck.ParameterGenerator;

import java.util.Random;

public class FloatGen implements ParameterGenerator<Float> {
    private final DoubleGen doubleGen;

    public FloatGen(String configuration) {
        doubleGen = new DoubleGen(configuration);
    }

    public Float generate() {
        return (float) (double) doubleGen.generate();
    }
}