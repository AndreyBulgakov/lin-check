package com.devexperts.dxlab.lincheck.tests.custom.counter;

import java.util.concurrent.atomic.AtomicInteger;

public class CounterCorrect2 implements Counter {
    private AtomicInteger c;

    public CounterCorrect2() {
        c = new AtomicInteger();
    }

    @Override
    public synchronized int incrementAndGet() {
        return c.incrementAndGet();
    }
}
