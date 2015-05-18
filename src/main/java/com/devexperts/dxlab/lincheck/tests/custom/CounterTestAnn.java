package com.devexperts.dxlab.lincheck.tests.custom;

import com.devexperts.dxlab.lincheck.CheckerAnnotated;
import com.devexperts.dxlab.lincheck.annotations.ActorAnn;
import com.devexperts.dxlab.lincheck.annotations.CTest;
import com.devexperts.dxlab.lincheck.annotations.Reload;
import com.devexperts.dxlab.lincheck.tests.custom.counter.Counter;
import com.devexperts.dxlab.lincheck.tests.custom.counter.CounterSynchronized;
import com.devexperts.dxlab.lincheck.tests.custom.counter.CounterWithoutAnySync;
import com.devexperts.dxlab.lincheck.tests.custom.queue.Queue;
import com.devexperts.dxlab.lincheck.tests.custom.queue.QueueEmptyException;
import com.devexperts.dxlab.lincheck.tests.custom.queue.QueueFullException;
import com.devexperts.dxlab.lincheck.tests.custom.queue.QueueWithoutAnySync;
import com.devexperts.dxlab.lincheck.util.Result;

import java.lang.reflect.InvocationTargetException;
import java.util.logging.Logger;

@CTest(iter = 20, actorsPerThread = {"1:3", "1:3"})
@CTest(iter = 20, actorsPerThread = {"1:3", "1:3", "1:3"})
public class CounterTestAnn {
    public Counter counter;

    @Reload
    public void reload() {
        counter = new CounterWithoutAnySync();
    }

    @ActorAnn(name = "incAndGet", args = {})
    public void actor1(Result res, Object[] args) {
        Integer v = counter.incrementAndGet();
        res.setValue(v);
    }

    public static void main(String[] args) throws InvocationTargetException, IllegalAccessException {
        CheckerAnnotated checker = new CheckerAnnotated();
        CounterTestAnn c = new CounterTestAnn();
        boolean result = checker.checkAnnotated(c);

        System.out.println(c.counter.getClass().getSimpleName() + " " + (result ? "error not found" : "error found"));
    }
}
