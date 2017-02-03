package com.devexperts.dxlab.lincheck.generators;

import com.devexperts.dxlab.lincheck.ParameterGenerator;

public class ByteGen implements ParameterGenerator<Byte> {
    private final IntGen intGen;

    public ByteGen(String configuration) {
        intGen = new IntGen(configuration);
        intGen.checkRange(Byte.MIN_VALUE, Byte.MAX_VALUE, "byte");
    }

    public Byte generate() {
        return (byte) (int) intGen.generate();
    }
}
