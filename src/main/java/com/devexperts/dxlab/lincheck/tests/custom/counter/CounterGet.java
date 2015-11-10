package com.devexperts.dxlab.lincheck.tests.custom.counter;

public class CounterGet{
    private int c;

    public CounterGet() {
        c = 0;
    }

    public int get() {
        return c;
    }

    public synchronized int incrementAndGet() {
        return ++c;
    }
}
