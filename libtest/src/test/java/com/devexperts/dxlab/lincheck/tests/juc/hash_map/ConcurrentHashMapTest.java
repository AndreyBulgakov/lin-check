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

package com.devexperts.dxlab.lincheck.tests.juc.hash_map;

import com.devexperts.dxlab.lincheck.Checker;
import com.devexperts.dxlab.lincheck.annotations.*;
import com.devexperts.dxlab.lincheck.generators.IntegerParameterGenerator;
import org.junit.Test;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;


@CTest(iter = 300, actorsPerThread = {"1:3", "1:3"})
@CTest(iter = 300, actorsPerThread = {"1:3", "1:3", "1:3"})
@Param(name = "key", generator = IntegerParameterGenerator.class)
@Param(name = "value", generator = IntegerParameterGenerator.class)
public class ConcurrentHashMapTest {
    public Map<Integer, Integer> m;

    @Reset
    public void reload() {
        m = new ConcurrentHashMap<>();
    }

    @Operation(params = {"key","value"})
    public int put(Integer key, Integer value) throws Exception {
        return m.put(key, value);
    }

    @Operation
    public int get(@Param(name = "key") Integer key) throws Exception {
        return m.get(key);
    }

    @Test
    public void test() throws Exception {
        assertTrue(Checker.check(new ConcurrentHashMapTest()));
    }
}
