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
import com.devexperts.dxlab.lincheck.annotations.CTest;
import com.devexperts.dxlab.lincheck.annotations.Operation;
import com.devexperts.dxlab.lincheck.annotations.Param;
import com.devexperts.dxlab.lincheck.annotations.Reset;
import com.devexperts.dxlab.lincheck.generators.IntegerParameterGenerator;
import org.junit.Test;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.Assert.assertTrue;


@CTest(iterations = 300, actorsPerThread = {"1:3", "1:3"})
@CTest(iterations = 300, actorsPerThread = {"1:3", "1:3", "1:3"})
@Param(name = "count", generator = IntegerParameterGenerator.class)
public class SetCorrect1 {
    public Set<Integer> q;

    @Reset
    public void reload() {
        q = Collections.newSetFromMap(new ConcurrentHashMap<Integer, Boolean>());
    }

    @Operation(params = {"count"})
    public boolean add(Integer params) throws Exception {
        return q.add(params);
    }

    @Operation(params = {"count"})
    public boolean remove(Integer params) throws Exception {
        return q.remove(params);
    }

    @Test
    public void test() throws Exception {
        assertTrue(Checker.check(new SetCorrect1()));
    }
}
