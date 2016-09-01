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

package com.devexperts.dxlab.lincheck.tests.boundary;

import com.devexperts.dxlab.lincheck.Checker;
import com.devexperts.dxlab.lincheck.annotations.CTest;
import com.devexperts.dxlab.lincheck.annotations.Operation;
import com.devexperts.dxlab.lincheck.annotations.Param;
import com.devexperts.dxlab.lincheck.annotations.Reset;
import com.devexperts.dxlab.lincheck.generators.IntegerGenerator;
import org.cliffc.high_scale_lib.NonBlockingHashMapLong;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertTrue;


@CTest(iter = 300, actorsPerThread = {"1:3", "1:3"})
@CTest(iter = 300, actorsPerThread = {"1:3", "1:3", "1:3"})
@Param(name = "key", clazz = IntegerGenerator.class)
@Param(name = "value", clazz = IntegerGenerator.class)
public class MapLongCorrect1 {
    public Map<Long, Integer> q;

    @Reset
    public void reload() {
        q = new NonBlockingHashMapLong<>();
    }

    @Operation(params = {"key","value"})
    public int put(Integer key, Integer value) throws Exception {
        Long k = key.longValue();
        return q.put(k, value);
    }

    @Operation
    public int get(@Param(name = "key") Integer key) throws Exception {
        return q.get(key);
    }

    @Operation()
    public int size() throws Exception {
        return q.size();
    }

    @Test
    public void test() throws Exception {
        assertTrue(Checker.check(new MapLongCorrect1()));
    }
}
