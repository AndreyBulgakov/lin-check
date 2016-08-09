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

import com.devexperts.dxlab.lincheck.Checker;
import com.devexperts.dxlab.lincheck.annotations.*;
import com.devexperts.dxlab.lincheck.SimpleGenerators.IntegerGenerator;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

@CTest(iter = 300, actorsPerThread = {"1:3", "1:3"})
@CTest(iter = 300, actorsPerThread = {"1:3", "1:3", "1:3"})
public class WrapperQueueCorrect1 {
    public Queue queue;

    @Reset
    public void reload() {
        queue = new QueueSynchronized(10);
    }

    @Operation
    public void put(@Param(clazz = IntegerGenerator.class)Integer args) throws Exception {
        //Integer x = (Integer) args[0];
        queue.put(args);
    }

    @Operation
    public Integer get() throws Exception {
        return queue.get();
        //res.setValue(value);
    }

    @Test
    public void test() throws Exception {
        Checker checker = new Checker();
        assertTrue(checker.checkAnnotated(new WrapperQueueCorrect1()));
    }
}
