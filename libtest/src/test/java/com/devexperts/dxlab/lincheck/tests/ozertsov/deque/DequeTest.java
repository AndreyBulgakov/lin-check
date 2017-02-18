package com.devexperts.dxlab.lincheck.tests.ozertsov.deque;

import com.devexperts.dxlab.lincheck.LinChecker;
import com.devexperts.dxlab.lincheck.annotations.CTest;
import com.devexperts.dxlab.lincheck.annotations.Operation;
import com.devexperts.dxlab.lincheck.annotations.Param;
import com.devexperts.dxlab.lincheck.annotations.Reset;
import com.devexperts.dxlab.lincheck.generators.IntGen;
import org.junit.Test;
import ozertsov.deque.LockFreeDeque;

/**
 * Created by alexander on 18.02.17.
 */
@CTest(iterations = 10, actorsPerThread = {"1:4", "1:4"}, invocationsPerIteration = 100_000)
public class DequeTest {
    private LockFreeDeque<Integer> lfdeque;

    @Reset
    public void reload() {
        lfdeque = new LockFreeDeque<>();
    }

    @Operation
    public void add(@Param(gen = IntGen.class) int value) {
        lfdeque.add(value);
    }

    @Operation
    public boolean contains(@Param(gen = IntGen.class) int value) {
        return lfdeque.contains(value);
    }

    @Operation
    public boolean isEmpty(){
        return lfdeque.isEmpty();
    }

    @Operation
    public Integer peek() {
        return lfdeque.peek();
    }

    @Operation
    public int size() {
        return lfdeque.size();
    }

    @Operation
    public Integer poll() {
        return lfdeque.poll();
    }

    @Test
    public void test() {
        LinChecker.check(this);
    }
}
