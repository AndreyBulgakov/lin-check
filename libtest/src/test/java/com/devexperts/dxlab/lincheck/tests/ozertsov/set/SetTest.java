package com.devexperts.dxlab.lincheck.tests.ozertsov.set;

import com.devexperts.dxlab.lincheck.LinChecker;
import com.devexperts.dxlab.lincheck.annotations.CTest;
import com.devexperts.dxlab.lincheck.annotations.Operation;
import com.devexperts.dxlab.lincheck.annotations.Param;
import com.devexperts.dxlab.lincheck.annotations.Reset;
import com.devexperts.dxlab.lincheck.generators.IntGen;
import org.junit.Test;
import ozertsov.set.LockFreeSet;

/**
 * Created by alexander on 18.02.17.
 */
@CTest(iterations = 10, actorsPerThread = {"1:3", "1:3"}, invocationsPerIteration = 100_000)
public class SetTest {
    private LockFreeSet<Integer> lfset;

    @Reset
    public void reload() {
        lfset = new LockFreeSet<>();
    }

    @Operation
    public void add(@Param(gen = IntGen.class) int value) {
        lfset.add(value);
    }

    @Operation
    public boolean contains(@Param(gen = IntGen.class) int value) {
        return lfset.contains(value);
    }

    @Operation
    public int size() {
        return lfset.size();
    }

    @Test
    public void test() {
        LinChecker.check(this);
    }
}
