package com.devexperts.dxlab.lincheck.tests.juc;

import com.devexperts.dxlab.lincheck.CheckerAnnotated;
import com.devexperts.dxlab.lincheck.annotations.ActorAnn;
import com.devexperts.dxlab.lincheck.annotations.CTest;
import com.devexperts.dxlab.lincheck.annotations.Reload;
import com.devexperts.dxlab.lincheck.util.Result;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;


@CTest(iter = 20, actorsPerThread = {"1:3", "1:3"})
@CTest(iter = 20, actorsPerThread = {"1:3", "1:3", "1:3"})
public class BlockingQueueTest {
    public ArrayDeque<Integer> q;

    @Reload
    public void reload() {
        q = new ArrayDeque<Integer>(2);
    }

    @ActorAnn(name = "add", args = {"1:10"})
    public void actor1(Result res, Object[] args) {
        Integer value = (Integer) args[0];

        try {
            boolean ret = q.add(value);
            res.setValue(ret ? 1 : 0);
        } catch (Exception e) {
            res.setException(e);
        }
    }

    @ActorAnn(name = "element", args = {})
    public void actor2(Result res, Object[] args) {
        try {
            Integer value = q.element();
            res.setValue(value);
        } catch (Exception e) {
            res.setException(e);
        }
    }

    public static void main(String[] args) throws InvocationTargetException, IllegalAccessException {
        CheckerAnnotated checker = new CheckerAnnotated();
        System.out.println(checker.checkAnnotated(new BlockingQueueTest()));
    }
}

