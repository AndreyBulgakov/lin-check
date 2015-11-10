package com.devexperts.dxlab.lincheck.tests.custom.counter;

import com.devexperts.dxlab.lincheck.CheckerAnnotatedASM;
import com.devexperts.dxlab.lincheck.annotations.ActorAnn;
import com.devexperts.dxlab.lincheck.annotations.CTest;
import com.devexperts.dxlab.lincheck.annotations.Reload;
import com.devexperts.dxlab.lincheck.util.Result;
import org.junit.Test;

import static junit.framework.TestCase.assertTrue;

@CTest(iter = 300, actorsPerThread = {"1:3", "1:3"})
@CTest(iter = 300, actorsPerThread = {"1:3", "1:3", "1:3"})
public class CounterTest2 {
    public Counter counter;

    @Reload
    public void reload() {
        counter = new CounterCorrect2();
    }

    @ActorAnn(args = {})
    public void incAndGet(Result res, Object[] args) {
        Integer v = counter.incrementAndGet();
        res.setValue(v);
    }

    @Test
    public void test() throws Exception {
        CheckerAnnotatedASM checker = new CheckerAnnotatedASM();
        assertTrue(checker.checkAnnotated(new CounterTest2()));
    }
}
