package com.devexperts.dxlab.lincheck.tests.custom.counter;

public class CounterWithoutAnySync implements Counter {
    private int c;

    public CounterWithoutAnySync() {
        c = 0;
    }

    @Override
    public int incrementAndGet() {
        c++;
        return c;
    }
}
