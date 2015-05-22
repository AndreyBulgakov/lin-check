package com.devexperts.dxlab.lincheck.tests.jctools;

import com.devexperts.dxlab.lincheck.CheckerAnnotatedASM;
import com.devexperts.dxlab.lincheck.annotations.ActorAnn;
import com.devexperts.dxlab.lincheck.annotations.CTest;
import com.devexperts.dxlab.lincheck.annotations.Immutable;
import com.devexperts.dxlab.lincheck.annotations.Reload;
import com.devexperts.dxlab.lincheck.util.Result;
import org.jctools.queues.QueueFactory;
import org.jctools.queues.spec.ConcurrentQueueSpec;
import org.junit.Test;

import java.util.Queue;

import static org.junit.Assert.assertTrue;

//@CTest(iter = 100, actorsPerThread = {"1:3", "1:3"})
@CTest(iter = 200, actorsPerThread = {"1:3", "1:3", "1:3"})
public class IQueueCorrect2 {
    public Queue<Integer> q;

    @Reload
    public void reload() {
//        q = QueueFactory.newQueue(ConcurrentQueueSpec.createBoundedMpmc(1));
        q = QueueFactory.newQueue(ConcurrentQueueSpec.createBoundedMpmc(1));
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

    @Immutable
    @ActorAnn(args = {})
    public void peek(Result res, Object[] args) throws Exception {
        res.setValue(q.peek());
    }


//    @ActorAnn(args = {"1:10"})
//    public void add(Result res, Object[] args) throws Exception {
//
//        Integer value = (Integer) args[0];
//        res.setValue(q.add(value));
//    }
//
//
//    @Immutable
//    @ActorAnn(args = {})
//    public void element(Result res, Object[] args)  throws Exception  {
//        Integer value = q.element();
//        res.setValue(value);
//    }
//
//    @ActorAnn(args = {"1:10"})
//    public void remove(Result res, Object[] args) throws Exception {
//        Integer ret = q.remove();
//        res.setValue(ret);
//    }

    @Test
    public void test() throws Exception {
        assertTrue(CheckerAnnotatedASM.check(new IQueueCorrect2()));
    }
}
