package com.devexperts.dxlab.lincheck.tests.custom;

import com.devexperts.dxlab.lincheck.CheckerAnnotated;
import com.devexperts.dxlab.lincheck.annotations.ActorAnn;
import com.devexperts.dxlab.lincheck.annotations.CTest;
import com.devexperts.dxlab.lincheck.annotations.Reload;
import com.devexperts.dxlab.lincheck.tests.custom.counter.Counter;
import com.devexperts.dxlab.lincheck.tests.custom.counter.CounterWithoutAnySync;
import com.devexperts.dxlab.lincheck.util.Result;

import java.lang.reflect.InvocationTargetException;

    @CTest(iter = 20, actorsPerThread = {"1:3", "1:3"})
    @CTest(iter = 20, actorsPerThread = {"1:3", "1:3", "1:3"})
    public class CounterTestAnnOut {
        private Counter counter;

        @Reload
        public void reload() {
            counter = new CounterWithoutAnySync();
        }

        @ActorAnn(args = {})
        public void incAndGet(Result res, Object[] args) {
            Integer v = counter.incrementAndGet();
            res.setValue(v);
        }
    }





