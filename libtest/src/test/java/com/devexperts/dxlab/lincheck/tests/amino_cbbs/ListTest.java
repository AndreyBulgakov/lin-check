package com.devexperts.dxlab.lincheck.tests.amino_cbbs;

import com.devexperts.dxlab.lincheck.LinChecker;
import com.devexperts.dxlab.lincheck.annotations.*;
import com.devexperts.dxlab.lincheck.generators.IntGen;
import org.junit.Test;
import amino_cbbs.LockFreeList;

/**
 * Created by alexander on 17.02.17.
 */
@CTest(iterations = 100, actorsPerThread = {"1:3", "1:3"}, invocationsPerIteration = 100_000)
public class ListTest {
    private LockFreeList<Integer> lflist;

    @Reset
    public void reload() {
        lflist = new LockFreeList<>();
    }

    @Operation
    public void add(@Param(gen = IntGen.class) int value) {
        lflist.add(value);
    }

    @Operation
    public boolean contains(@Param(gen = IntGen.class) int value) {
        return lflist.contains(value);
    }

    @Operation
    public int indexOf(@Param(gen = IntGen.class) int value) {
        return lflist.indexOf(value);
    }

    @Operation
    @HandleExceptionAsResult(NullPointerException.class)
    public int get(@Param(gen = IntGen.class) int index) {
        return lflist.get(index);
    }

    @Operation
    public int size() {
        return lflist.size();
    }

    @Operation
    @HandleExceptionAsResult(IndexOutOfBoundsException.class)
    public void set(@Param(gen = IntGen.class) int index, @Param(gen = IntGen.class) int value) {
        lflist.set(index, value);
    }

    @Test
    public void test() {
        LinChecker.check(this);
    }
}
