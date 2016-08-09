/*
 *  Lincheck - Linearizability checker
 *  Copyright (C) 2015 Devexperts LLC
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.devexperts.dxlab.lincheck.tests.guava;

import com.devexperts.dxlab.lincheck.Checker;
import com.devexperts.dxlab.lincheck.annotations.*;
import com.devexperts.dxlab.lincheck.SimpleGenerators.IntegerGenerator;
import com.google.common.collect.ConcurrentHashMultiset;
import org.junit.Test;

import static org.junit.Assert.assertTrue;



@CTest(iter = 300, actorsPerThread = {"1:3", "1:3"})
@CTest(iter = 300, actorsPerThread = {"1:3", "1:3", "1:3"})
@Param(name = "value", clazz = IntegerGenerator.class)
@Param(name = "count", clazz = IntegerGenerator.class)
public class MultisetCorrect1 {
    public ConcurrentHashMultiset<Integer> q;

    @Reset
    public void reload() {
        q = ConcurrentHashMultiset.create();
    }

    @Operation(params = {"value", "count"})
    public int add(int value, int count) throws Exception {
        return q.add(value, count);
    }

    @Operation(params = {"value", "count"})
    public int remove(int value, int count) throws Exception {
        return q.remove(value, count);
    }

    @Test
    public void test() throws Exception {
        assertTrue(Checker.check(new MultisetCorrect1()));
    }
}
