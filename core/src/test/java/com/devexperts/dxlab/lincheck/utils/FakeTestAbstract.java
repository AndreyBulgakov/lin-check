package com.devexperts.dxlab.lincheck.utils;

// TODO bad package name. Package should have prefix "com.devexperts.lincheck."
import com.devexperts.dxlab.transformigclasses.A;

/**
 * Created by andrey on 2/20/17.
 */
public abstract class FakeTestAbstract {

    public abstract A getA();
    public abstract String getString();
    public abstract void invoke();

}
