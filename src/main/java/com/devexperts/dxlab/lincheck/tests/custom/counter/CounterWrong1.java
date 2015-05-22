package com.devexperts.dxlab.lincheck.tests.custom.counter;

public class CounterWrong1 implements Counter {
    private int c;

    public CounterWrong1() {
        c = 0;
    }

    @Override
    public int incrementAndGet() {
        c++;
        return c;
    }
}
