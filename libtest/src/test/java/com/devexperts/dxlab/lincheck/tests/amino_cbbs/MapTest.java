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

import amino_cbbs.LockFreeMap;
import com.devexperts.dxlab.lincheck.LinChecker;
import com.devexperts.dxlab.lincheck.annotations.*;
import com.devexperts.dxlab.lincheck.generators.IntGen;
import org.junit.Test;

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
