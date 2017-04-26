package com.devexperts.dxlab.lincheck.tests.amino_cbbs;

/*
 * #%L
 * libtest
 * %%
 * Copyright (C) 2015 - 2017 Devexperts, LLC
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

import amino_cbbs.LockFreeList;
import com.devexperts.dxlab.lincheck.LinChecker;
import com.devexperts.dxlab.lincheck.annotations.*;
import com.devexperts.dxlab.lincheck.generators.IntGen;
import org.junit.Test;

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
