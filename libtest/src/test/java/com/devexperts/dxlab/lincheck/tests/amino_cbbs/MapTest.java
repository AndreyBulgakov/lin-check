package com.devexperts.dxlab.lincheck.tests.amino_cbbs;

import com.devexperts.dxlab.lincheck.LinChecker;
import com.devexperts.dxlab.lincheck.annotations.*;
import com.devexperts.dxlab.lincheck.generators.IntGen;
import org.junit.Test;
import amino_cbbs.LockFreeMap;

/**
 * Created by alexander on 18.02.17.
 */
@CTest(iterations = 100, actorsPerThread = {"1:3", "1:3"}, invocationsPerIteration = 100_000)
public class MapTest {
    private LockFreeMap<Integer, Integer> lfmap;

    @Reset
    public void reload() {
        lfmap = new LockFreeMap<>();
    }

    @Operation
    public void put(@Param(gen = IntGen.class) int key, @Param(gen = IntGen.class) int value) {
        lfmap.put(key, value);
    }

    @Operation
    public boolean containsKey(@Param(gen = IntGen.class) int key) {
        return lfmap.containsKey(key);
    }

    @Operation
    public boolean containsValue(@Param(gen = IntGen.class) int value) {
        return lfmap.containsKey(value);
    }

    @Operation
    public int size() {
        return lfmap.size();
    }

    @Operation
    @HandleExceptionAsResult(NullPointerException.class)
    public int get(@Param(gen = IntGen.class) int key) {
        return lfmap.get(key);
    }

    @Test
    public void test() {
        LinChecker.check(this);
    }
}
