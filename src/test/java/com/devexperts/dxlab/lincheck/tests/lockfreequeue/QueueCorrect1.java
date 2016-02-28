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

package com.devexperts.dxlab.lincheck.tests.lockfreequeue;

import com.devexperts.dxlab.lincheck.Checker;
import com.devexperts.dxlab.lincheck.annotations.*;
import com.devexperts.dxlab.lincheck.annotations.Operation;
import com.devexperts.dxlab.lincheck.util.Result;
import com.github.lock.free.queue.LockFreeQueue;
import org.junit.Test;

import static org.junit.Assert.assertTrue;


/**
 * https://github.com/yaitskov/lock-free-queue
 */

//@CTest(iter = 300, actorsPerThread = {"1:3", "1:3"})
@CTest(iter = 300, actorsPerThread = {"1:3", "1:3", "1:3"})
public class QueueCorrect1 {
    public LockFreeQueue<Integer> q;

    @Reload
    public void reload() {
        q = new LockFreeQueue<>();
    }

    @Operation(args = {"1:10"})
    public void add(Result res, Object[] args) throws Exception {
        Integer value = (Integer) args[0];
        q.add(value);
        res.setVoid();
    }

    @Operation(args = {})
    public void takeOrNull(Result res, Object[] args) throws Exception {
        res.setValue(q.takeOrNull());
    }

    @Test
    public void test() throws Exception {
        assertTrue(Checker.check(new QueueCorrect1()));
        // TODO failed test

    }
}
