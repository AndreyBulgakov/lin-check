package com.devexperts.dxlab.lincheck.tests.custom.counter;

public class CounterCorrect1 implements Counter {
    private int c;

    public CounterCorrect1() {
        c = 0;
    }

    @Override
    public synchronized int incrementAndGet() {
        c++;
        return c;
    }
}
