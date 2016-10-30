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

package com.devexperts.dxlab.lincheck.tests.zchannel;

import com.devexperts.dxlab.lincheck.Checker;
import com.devexperts.dxlab.lincheck.annotations.CTest;
import com.devexperts.dxlab.lincheck.annotations.Operation;
import com.devexperts.dxlab.lincheck.annotations.Param;
import com.devexperts.dxlab.lincheck.annotations.Reset;
import com.devexperts.dxlab.lincheck.generators.IntegerParameterGenerator;
import org.junit.Test;
import z.channel.GenericMPMCQueue;

import static org.junit.Assert.assertTrue;


/**
 * http://landz.github.io/
 */

//@CTest(iterations = 300, actorsPerThread = {"1:3", "1:3"})
@CTest(iterations = 300, actorsPerThread = {"1:3", "1:3", "1:3"})
public class QueueCorrect1 {
    public GenericMPMCQueue<Integer> q;

    @Reset
    public void reload() {
        q = new GenericMPMCQueue(2);
    }

    @Operation
    public boolean offer(@Param(generator = IntegerParameterGenerator.class) int value) throws Exception {
        return q.offer(value);
    }

    @Operation
    public int poll() throws Exception {
        return q.poll();
    }

    @Test
    public void test() throws Exception {
        assertTrue(Checker.check(new QueueCorrect1()));
        // TODO failed test
    }
}
