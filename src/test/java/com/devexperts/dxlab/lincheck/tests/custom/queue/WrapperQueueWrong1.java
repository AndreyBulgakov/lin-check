package com.devexperts.dxlab.lincheck.tests.custom.queue;

import com.devexperts.dxlab.lincheck.CheckerAnnotated;
import com.devexperts.dxlab.lincheck.CheckerAnnotatedASM;
import com.devexperts.dxlab.lincheck.annotations.ActorAnn;
import com.devexperts.dxlab.lincheck.annotations.CTest;
import com.devexperts.dxlab.lincheck.annotations.Reload;
import com.devexperts.dxlab.lincheck.util.Result;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;

import static org.junit.Assert.assertFalse;
@CTest(iter = 200, actorsPerThread = {"1:3", "1:3"})
@CTest(iter = 200, actorsPerThread = {"1:3", "1:3", "1:3"})
public class WrapperQueueWrong1 {
    public Queue queue;

    @Reload
    public void reload() {
        queue = new QueueWrong1(10);
    }

    @ActorAnn(args = {"1:10"})
    public void put(Result res, Object[] args) throws QueueFullException {
        Integer x = (Integer) args[0];
        queue.put(x);
        res.setVoid();
    }

    @ActorAnn(args = {})
    public void get(Result res, Object[] args) throws Exception {
        Integer value = queue.get();
        res.setValue(value);
    }

    @Test
    public void test() throws Exception {
        CheckerAnnotatedASM checker = new CheckerAnnotatedASM();
        assertFalse(checker.checkAnnotated(new WrapperQueueWrong1()));
    }
}
