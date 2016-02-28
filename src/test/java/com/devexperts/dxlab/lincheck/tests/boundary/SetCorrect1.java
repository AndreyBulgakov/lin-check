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
import com.devexperts.dxlab.lincheck.annotations.Operation;
import com.devexperts.dxlab.lincheck.annotations.CTest;
import com.devexperts.dxlab.lincheck.annotations.Reload;
import com.devexperts.dxlab.lincheck.util.MyRandom;
import com.devexperts.dxlab.lincheck.util.Result;
import org.cliffc.high_scale_lib.NonBlockingHashSet;
import org.junit.Test;

import static org.junit.Assert.assertTrue;


@CTest(iter = 300, actorsPerThread = {"1:3", "1:3"})
@CTest(iter = 300, actorsPerThread = {"1:3", "1:3", "1:3"})
public class SetCorrect1 {
    public NonBlockingHashSet<Integer> q;

    @Reload
    public void reload() {
        q = new NonBlockingHashSet<>();
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

    @Operation(args = {})
    public void size(Result res, Object[] args) throws Exception {
        res.setValue(q.size());
    }

    @Test
    public void test() throws Exception {
        MyRandom.nextInt();
        assertTrue(Checker.check(new SetCorrect1()));
        // TODO failed test

    }
}
