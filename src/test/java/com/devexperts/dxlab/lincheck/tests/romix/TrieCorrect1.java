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

package com.devexperts.dxlab.lincheck.tests.romix;

import java.util.Map;

import com.devexperts.dxlab.lincheck.Checker;
import com.devexperts.dxlab.lincheck.annotations.*;
import com.devexperts.dxlab.lincheck.generators.IntegerParameterGenerator;
import romix.scala.collection.concurrent.TrieMap;
import org.junit.Test;

import static junit.framework.TestCase.assertTrue;


@CTest(iter = 300, actorsPerThread = {"1:3", "1:3"})
@CTest(iter = 300, actorsPerThread = {"1:3", "1:3", "1:3"})
@Param(name = "key", generator = IntegerParameterGenerator.class)
@Param(name = "value", generator = IntegerParameterGenerator.class)
public class TrieCorrect1 {
    public Map<Integer, Integer> m;

    @Reset
    public void reload() {
        m = new TrieMap<>();
    }

    @Operation(params = {"key","value"})
    public int put(int key, int value) throws Exception {
        return m.put(key, value);
    }

    @ReadOnly
    @Operation(params = {"key"})
    public int get(int key) throws Exception {
        return m.get(key);
    }

    @Test
    public void test() throws Exception {
        assertTrue(Checker.check(new TrieCorrect1()));
    }
}

