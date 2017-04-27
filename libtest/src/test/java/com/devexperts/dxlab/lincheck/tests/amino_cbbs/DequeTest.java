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

import amino_cbbs.LockFreeDeque;
import com.devexperts.dxlab.lincheck.LinChecker;
import com.devexperts.dxlab.lincheck.annotations.CTest;
import com.devexperts.dxlab.lincheck.annotations.Operation;
import com.devexperts.dxlab.lincheck.annotations.Param;
import com.devexperts.dxlab.lincheck.annotations.Reset;
import com.devexperts.dxlab.lincheck.generators.IntGen;
import org.junit.Test;

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

//    @Operation
//    public Boolean contains(@Param(gen = IntGen.class) int value) {
//        return lfdeque.contains(value);
//    }

    @Operation
    public Boolean isEmpty() {
        Boolean bool = lfdeque.isEmpty();
        return bool;
//        return lfdeque.isEmpty();
    }

    @Operation
    public Integer peek() {
        return lfdeque.peek();
    }

    @Operation
    public Integer size() {
        Integer integer = lfdeque.size();
        return integer;
//        return lfdeque.size();
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
