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

package com.devexperts.dxlab.lincheck.tests.juc.blocking_queue;

import com.devexperts.dxlab.lincheck.Checker;
import com.devexperts.dxlab.lincheck.annotations.*;
import com.devexperts.dxlab.lincheck.generators.IntegerGenerator;
import org.junit.Test;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import static org.junit.Assert.assertTrue;


@CTest(iter = 300, actorsPerThread = {"1:3", "1:3"})
@CTest(iter = 300, actorsPerThread = {"1:3", "1:3", "1:3"})
public class BlockingQueueTest4 {
    public Queue<Integer> q;

    @Reset
    public void reload() {
        q = new ConcurrentLinkedQueue<>();
    }

    @Operation
    public boolean add(@Param(clazz = IntegerGenerator.class)Integer value) throws Exception {

        return q.add(value);
    }

    @ReadOnly
    @Operation
    public int element()  throws Exception  {
        return q.element();
    }

    @Operation
    public int remove() throws Exception {
        return q.remove();
    }

    @Operation
    public int poll() throws Exception {
        return q.poll();
    }


    @Test
    public void test() throws Exception {
        assertTrue(Checker.check(new BlockingQueueTest4()));
    }
}

