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

package com.devexperts.dxlab.lincheck.tests.jctools;

import com.devexperts.dxlab.lincheck.Checker;
import com.devexperts.dxlab.lincheck.annotations.*;
import com.devexperts.dxlab.lincheck.annotations.ReadOnly;
import com.devexperts.dxlab.lincheck.util.Result;
import org.jctools.queues.QueueFactory;
import org.jctools.queues.spec.ConcurrentQueueSpec;
import org.junit.Test;

import java.util.Queue;

import static org.junit.Assert.assertTrue;

//@CTest(iter = 300, actorsPerThread = {"1:3", "1:3"})
@CTest(iter = 300, actorsPerThread = {"1:3", "1:3", "1:3"})
public class IQueueCorrect2 {
    public Queue<Integer> q;

    @Reload
    public void reload() {
//        q = QueueFactory.newQueue(ConcurrentQueueSpec.createBoundedMpmc(1));
        q = QueueFactory.newQueue(ConcurrentQueueSpec.createBoundedMpmc(1));
    }


    @Operation(args = {"1:10"})
    public void offer(Result res, Object[] args) throws Exception {
        Integer value = (Integer) args[0];
        res.setValue(q.offer(value));
    }

    @Operation(args = {})
    public void poll(Result res, Object[] args) throws Exception {
        res.setValue(q.poll());
    }

    @ReadOnly
    @Operation(args = {})
    public void peek(Result res, Object[] args) throws Exception {
        res.setValue(q.peek());
    }


//    @Operation(args = {"1:10"})
//    public void add(Result res, Object[] args) throws Exception {
//
//        Integer value = (Integer) args[0];
//        res.setValue(q.add(value));
//    }
//
//
//    @ReadOnly
//    @Operation(args = {})
//    public void element(Result res, Object[] args)  throws Exception  {
//        Integer value = q.element();
//        res.setValue(value);
//    }
//
//    @Operation(args = {"1:10"})
//    public void remove(Result res, Object[] args) throws Exception {
//        Integer ret = q.remove();
//        res.setValue(ret);
//    }


//    [0_offer(4), 2_peek(), 4_offer(8), 3_offer(3), 1_poll()]

    @Test
    public void test() throws Exception {
//        reload();
//
//        System.out.println(q.offer(4));
//        System.out.println(q.peek());
//        System.out.println(q.offer(8));
//        System.out.println(q.offer(3));
//        System.out.println(q.poll());
//
        assertTrue(Checker.check(new IQueueCorrect2()));
        // TODO failed test
    }
}
