package com.devexperts.dxlab.lincheck;

import com.devexperts.dxlab.lincheck.counter.Counter;
import com.devexperts.dxlab.lincheck.util.Result;

// TODO use main Tester class
public class CounterCaller {
    Counter counter;

    public CounterCaller(Counter counter) {
        this.counter = counter;
    }

    public void setCounter(Counter counter) {
        this.counter = counter;
    }

    /*
        0 - incrementAndGet()
    */
    Result call(int method) {
        Result res = new Result();

        if (method == 0) {
            Integer value = counter.incrementAndGet();
            res.setValue(value);
        }
        return res;
    }
}
