package com.devexperts.dxlab.lincheck.generators;

import com.devexperts.dxlab.lincheck.ParameterGenerator;

public class ShortGen implements ParameterGenerator<Short> {
    private final IntGen intGen;

    public ShortGen(String configuration) {
        intGen = new IntGen(configuration);
        intGen.checkRange(Short.MIN_VALUE, Short.MAX_VALUE, "short");
    }

    public Short generate() {
        return (short) (int) intGen.generate();
    }
}
