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
import com.devexperts.dxlab.lincheck.annotations.Operation;
import com.devexperts.dxlab.lincheck.annotations.CTest;
import com.devexperts.dxlab.lincheck.annotations.Reload;
import com.devexperts.dxlab.lincheck.util.Result;
import org.junit.Test;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.Assert.assertTrue;


@CTest(iter = 300, actorsPerThread = {"1:3", "1:3"})
@CTest(iter = 300, actorsPerThread = {"1:3", "1:3", "1:3"})
public class SetCorrect1 {
    public Set<Integer> q;

    @Reload
    public void reload() {
        q = Collections.newSetFromMap(new ConcurrentHashMap<Integer, Boolean>());
    }

    @Operation(args = {"1:3"})
    public void add(Result res, Object[] args) throws Exception {
        Integer value = (Integer) args[0];
        res.setValue(q.add(value));
    }

    @Operation(args = {"1:3"})
    public void remove(Result res, Object[] args) throws Exception {
        Integer value = (Integer) args[0];
        res.setValue(q.remove(value));
    }

    @Test
    public void test() throws Exception {
        assertTrue(Checker.check(new SetCorrect1()));
    }
}
