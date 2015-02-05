package me.aevd.lintesting;

public class CounterCaller {
    Counter counter;

    public CounterCaller(Counter counter) {
        this.counter = counter;
    }

    /*
        0 - incrementAndGet()
    */
    int call(int method) {
        if (method == 0) {
            return counter.incrementAndGet();
        }
        return -1;
    }
}
