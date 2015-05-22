package com.devexperts.dxlab.lincheck.tests.lockfreequeue;

import com.devexperts.dxlab.lincheck.CheckerAnnotatedASM;
import com.devexperts.dxlab.lincheck.annotations.ActorAnn;
import com.devexperts.dxlab.lincheck.annotations.CTest;
import com.devexperts.dxlab.lincheck.annotations.Reload;
import com.devexperts.dxlab.lincheck.util.Result;
import com.github.lock.free.queue.LockFreeQueue;
import org.junit.Test;
import z.channel.GenericMPMCQueue;

import static org.junit.Assert.assertTrue;


/**
 * https://github.com/yaitskov/lock-free-queue
 */

@CTest(iter = 200, actorsPerThread = {"1:3", "1:3"})
@CTest(iter = 200, actorsPerThread = {"1:3", "1:3", "1:3"})
public class QueueCorrect1 {
    public LockFreeQueue<Integer> q;

    @Reload
    public void reload() {
        q = new LockFreeQueue<>();
    }

    @ActorAnn(args = {"1:10"})
    public void add(Result res, Object[] args) throws Exception {
        Integer value = (Integer) args[0];
        q.add(value);
        res.setVoid();
    }

    @ActorAnn(args = {})
    public void remove(Result res, Object[] args) throws Exception {
        res.setValue(q.takeOrNull());
    }

    @Test
    public void test() throws Exception {
        assertTrue(CheckerAnnotatedASM.check(new QueueCorrect1()));
        // TODO failed test

    }
}
