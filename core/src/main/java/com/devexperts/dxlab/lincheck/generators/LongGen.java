package com.devexperts.dxlab.lincheck.generators;

import com.devexperts.dxlab.lincheck.ParameterGenerator;

public class LongGen implements ParameterGenerator<Long> {
    private final IntGen intGen;

    public LongGen(String configuration) {
        intGen = new IntGen(configuration);
    }

    public Long generate() {
        return (long) intGen.generate();
    }
}
