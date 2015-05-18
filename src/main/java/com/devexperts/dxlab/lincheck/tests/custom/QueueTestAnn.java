package com.devexperts.dxlab.lincheck.tests.custom;

import com.devexperts.dxlab.lincheck.annotations.ActorAnn;
import com.devexperts.dxlab.lincheck.annotations.CTest;
import com.devexperts.dxlab.lincheck.annotations.Reload;
import com.devexperts.dxlab.lincheck.tests.custom.queue.Queue;
import com.devexperts.dxlab.lincheck.tests.custom.queue.QueueEmptyException;
import com.devexperts.dxlab.lincheck.tests.custom.queue.QueueFullException;
import com.devexperts.dxlab.lincheck.tests.custom.queue.QueueWithoutAnySync;
import com.devexperts.dxlab.lincheck.CheckerAnnotated;
import com.devexperts.dxlab.lincheck.util.Result;

import java.lang.reflect.InvocationTargetException;

@CTest(iter = 50, actorsPerThread = {"2:3", "2:3"})
//@CTest(iter = 20, actorsPerThread = {"1:3", "1:3", "1:3"})
public class QueueTestAnn {
    public Queue queue;

    @Reload
    public void reload() {
        queue = new QueueWithoutAnySync(10);
    }

    @ActorAnn(name = "put", args = {"1:10"})
    public void put(Result res, Object[] args) {
        Integer x = (Integer) args[0];
        try {
            queue.put(x);
            res.setVoid();
        } catch (QueueFullException e) {
            res.setException(e);
        }
    }

    @ActorAnn(name = "get", args = {})
    public void get(Result res, Object[] args) {
        try {
            Integer value = queue.get();
            res.setValue(value);
        } catch (QueueEmptyException e) {
            res.setException(e);
        }
    }

    public static void main(String[] args) throws InvocationTargetException, IllegalAccessException {
        CheckerAnnotated checker = new CheckerAnnotated();
        checker.checkAnnotated(new QueueTestAnn());
    }
}
