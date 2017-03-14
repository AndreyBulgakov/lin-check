package com.devexperts.dxlab.lincheck.utils;

import com.devexperts.dxlab.transformigclasses.A;

/**
 * Need for test loading and transforming Test classes
 */
public abstract class FakeTestAbstract {

    public abstract A getA();
    public abstract String getString();
    public abstract void invoke();

}
