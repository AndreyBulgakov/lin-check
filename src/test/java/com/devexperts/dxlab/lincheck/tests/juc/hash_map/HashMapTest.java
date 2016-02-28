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
import com.devexperts.dxlab.lincheck.annotations.Operation;
import com.devexperts.dxlab.lincheck.util.Result;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


@CTest(iter = 300, actorsPerThread = {"1:3", "1:3"})
@CTest(iter = 300, actorsPerThread = {"1:3", "1:3", "1:3"})
public class HashMapTest {
    public Map<Integer, Integer> m;

    @Reload
    public void reload() {
        m = new HashMap<>();
    }

    @Operation(args = {"1:4", "1:10"})
    public void put(Result res, Object[] args) throws Exception {
        Integer key = (Integer) args[0];
        Integer value = (Integer) args[1];
        Integer prevValue = m.put(key, value);
        res.setValue(prevValue);
    }

    @ReadOnly
    @Operation(args = {"1:4"})
    public void get(Result res, Object[] args) throws Exception {
        Integer key = (Integer) args[0];
        Integer value = m.get(key);
        res.setValue(value);
    }

    @Test
    public void test() throws Exception {
        Checker checker = new Checker();
        assertFalse(checker.checkAnnotated(new HashMapTest()));
    }
}

