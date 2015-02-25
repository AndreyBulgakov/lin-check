package me.aevd.lintesting;

import me.aevd.lintesting.counter.Counter;
import me.aevd.lintesting.util.Result;

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
