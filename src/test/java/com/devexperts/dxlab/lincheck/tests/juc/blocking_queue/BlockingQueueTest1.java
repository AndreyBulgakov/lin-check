package com.devexperts.dxlab.lincheck.tests.juc.blocking_queue;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import com.devexperts.dxlab.lincheck.CheckerAnnotatedASM;
import com.devexperts.dxlab.lincheck.annotations.*;
import com.devexperts.dxlab.lincheck.util.Result;
import org.junit.Test;

import static org.junit.Assert.assertTrue;


@CTest(iter = 300, actorsPerThread = {"1:3", "1:3"})
@CTest(iter = 300, actorsPerThread = {"1:3", "1:3", "1:3"})
public class BlockingQueueTest1 {
    public BlockingQueue<Integer> q;

    @Reload
    public void reload() {
        q = new ArrayBlockingQueue<Integer>(10);
    }

    @ActorAnn(args = {"1:10"})
    public void add(Result res, Object[] args) throws Exception {
        Integer value = (Integer) args[0];

        res.setValue(q.add(value));
    }

    @ReadOnly
    @ActorAnn(args = {})
    public void element(Result res, Object[] args)  throws Exception  {
        res.setValue(q.element());
    }

    @ActorAnn(args = {})
    public void remove(Result res, Object[] args) throws Exception {
        res.setValue(q.remove());
    }

    @ActorAnn(args = {})
    public void poll(Result res, Object[] args) throws Exception {
        res.setValue(q.poll());
    }


    @Test
    public void test() throws Exception {
        assertTrue(CheckerAnnotatedASM.check(new BlockingQueueTest1()));
    }
}

