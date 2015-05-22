package com.devexperts.dxlab.lincheck.tests.custom.counter;

public class CounterWrong2 implements Counter {
    private int c;

    public CounterWrong2() {
        c = 0;
    }

    @Override
    public int incrementAndGet() {
        return ++c;
    }
}
