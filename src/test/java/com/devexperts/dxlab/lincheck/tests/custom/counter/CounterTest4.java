package com.devexperts.dxlab.lincheck.tests.custom.counter;

import com.devexperts.dxlab.lincheck.CheckerAnnotatedASM;
import com.devexperts.dxlab.lincheck.annotations.ActorAnn;
import com.devexperts.dxlab.lincheck.annotations.CTest;
import com.devexperts.dxlab.lincheck.annotations.Reload;
import com.devexperts.dxlab.lincheck.util.Result;
import org.junit.Test;

import static org.junit.Assert.assertFalse;

@CTest(iter = 200, actorsPerThread = {"1:3", "1:3"})
@CTest(iter = 200, actorsPerThread = {"1:3", "1:3", "1:3"})
public class CounterTest4 {
    public Counter counter;

    @Reload
    public void reload() {
        counter = new CounterWrong2();
    }

    @ActorAnn(args = {})
    public void incAndGet(Result res, Object[] args) {
        Integer v = counter.incrementAndGet();
        res.setValue(v);
    }

    @Test
    public void test() throws Exception {
        assertFalse(CheckerAnnotatedASM.check(new CounterTest4()));
    }
}
