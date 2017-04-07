package com.devexperts.dxlab.lincheck.tests.counter;

import com.devexperts.dxlab.lincheck.LinChecker;
import com.devexperts.dxlab.lincheck.annotations.CTest;
import com.devexperts.dxlab.lincheck.annotations.Operation;
import com.devexperts.dxlab.lincheck.annotations.Reset;
import com.devexperts.dxlab.lincheck.libtest.counter.Counter;
import com.devexperts.dxlab.lincheck.libtest.counter.SimpleWrongCounter1;
import org.junit.Test;

/**
 * Created by alexander on 26.02.17.
 */
@CTest(iterations = 50, actorsPerThread = {"2:5", "2:5"})
public class SimpleWrongCounterTest1 {
    private Counter counter;


    @Reset
    public void reload() throws Exception {
        counter = new SimpleWrongCounter1();
    }

    @Operation
    public int incAndGet() {
        return counter.incrementAndGet();
    }

    @Test
    public void test() throws Exception {
        LinChecker.check(this);
    }
}
