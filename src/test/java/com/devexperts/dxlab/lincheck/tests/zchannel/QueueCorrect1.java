package com.devexperts.dxlab.lincheck.tests.zchannel;

import com.devexperts.dxlab.lincheck.CheckerAnnotatedASM;
import com.devexperts.dxlab.lincheck.annotations.ActorAnn;
import com.devexperts.dxlab.lincheck.annotations.CTest;
import com.devexperts.dxlab.lincheck.annotations.Immutable;
import com.devexperts.dxlab.lincheck.annotations.Reload;
import com.devexperts.dxlab.lincheck.util.Result;
import org.jctools.queues.QueueFactory;
import org.jctools.queues.spec.ConcurrentQueueSpec;
import org.junit.Test;
import z.channel.GenericMPMCQueue;
import z.channel.MPMCQueue;

import java.util.Queue;

import static org.junit.Assert.assertTrue;


/**
 * http://landz.github.io/
 */

@CTest(iter = 200, actorsPerThread = {"1:3", "1:3"})
@CTest(iter = 200, actorsPerThread = {"1:3", "1:3", "1:3"})
public class QueueCorrect1 {
    public GenericMPMCQueue<Integer> q;

    @Reload
    public void reload() {
        q = new GenericMPMCQueue(2);
    }

    @ActorAnn(args = {"1:10"})
    public void offer(Result res, Object[] args) throws Exception {
        Integer value = (Integer) args[0];
        res.setValue(q.offer(value));
    }

    @ActorAnn(args = {})
    public void poll(Result res, Object[] args) throws Exception {
        res.setValue(q.poll());
    }

    @Test
    public void test() throws Exception {
        assertTrue(CheckerAnnotatedASM.check(new QueueCorrect1()));
        // TODO failed test
    }
}
