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

package com.devexperts.dxlab.lincheck.tests.custom.queue;

import com.devexperts.dxlab.lincheck.LinChecker;
import com.devexperts.dxlab.lincheck.annotations.*;
import com.devexperts.dxlab.lincheck.generators.IntegerParameterGenerator;
import tests.custom.queue.Queue;
import tests.custom.queue.QueueEmptyException;
import tests.custom.queue.QueueFullException;
import tests.custom.queue.QueueWrong2;
import tests.custom.queue.QueueWrong3;
import org.junit.Test;

import static org.junit.Assert.assertFalse;

@CTest(iterations = 300, actorsPerThread = {"1:3", "1:3"})
@CTest(iterations = 300, actorsPerThread = {"1:3", "1:3", "1:3"})
public class WrapperQueueWrong3 {
    private Queue queue;

    @Reset
    public void reload() {
        queue = new QueueWrong3(10);
    }

    @Operation
    @HandleExceptionAsResult(QueueFullException.class)
    public void put(@Param(generator = IntegerParameterGenerator.class)int args) throws Exception {
        queue.put(args);
    }

    @Operation
    @HandleExceptionAsResult(QueueEmptyException.class)
    public int get() throws Exception {
        return queue.get();
    }

    @Test(expected = AssertionError.class)
    public void test() throws Exception {
        LinChecker.check(new WrapperQueueWrong2());
    }
}
