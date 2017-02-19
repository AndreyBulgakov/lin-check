package com.devexperts.dxlab.lincheck.tests.ozertsov.queue;

import com.devexperts.dxlab.lincheck.LinChecker;
import com.devexperts.dxlab.lincheck.annotations.CTest;
import com.devexperts.dxlab.lincheck.annotations.Operation;
import com.devexperts.dxlab.lincheck.annotations.Param;
import com.devexperts.dxlab.lincheck.annotations.Reset;
import com.devexperts.dxlab.lincheck.generators.IntGen;
import org.junit.Test;
import com.devexperts.dxlab.lincheck.libtest.ozertsov.queue.LockFreeQueue;
/**
 * Created by alexander on 13.02.17.
 */
@CTest(iterations = 100, actorsPerThread = {"1:5", "1:5"}, invocationsPerIteration = 100_000)
public class QueueTest {
    private LockFreeQueue<Integer> q;

    @Reset
    public void reload() {
        q = new LockFreeQueue<>();
    }

    @Operation
    public void add(@Param(gen = IntGen.class) int value) {
        q.add(value);
    }

    @Operation
    public int takeOrNull() {
        return q.takeOrNull();
    }

    @Test
    public void test() {
        LinChecker.check(this);
    }
}
