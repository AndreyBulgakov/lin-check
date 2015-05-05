package com.devexperts.dxlab.lincheck;

import com.devexperts.dxlab.lincheck.annotations.ActorAnn;
import com.devexperts.dxlab.lincheck.annotations.CTest;
import com.devexperts.dxlab.lincheck.annotations.Reload;
import com.devexperts.dxlab.lincheck.queue.Queue;
import com.devexperts.dxlab.lincheck.queue.QueueEmptyException;
import com.devexperts.dxlab.lincheck.queue.QueueFullException;
import com.devexperts.dxlab.lincheck.queue.QueueWithoutAnySync;
import com.devexperts.dxlab.lincheck.util.Actor;
import com.devexperts.dxlab.lincheck.util.CheckerAnnotated;
import com.devexperts.dxlab.lincheck.util.Result;

import java.lang.reflect.InvocationTargetException;

@CTest(iter = 2, actorsPerThread = {"1:3", "1:3"})
@CTest(iter = 2, actorsPerThread = {"1:3", "1:3", "1:3"})
public class QueueTestAnn {
    public Queue queue;

    @Reload
    public void reload() {
        queue = new QueueWithoutAnySync(10);
    }

    @ActorAnn(name = "put", args = {"1:10"})
    public void actor1(Result res, Object[] args) {
        Integer x = (Integer) args[0];
        try {
            queue.put(x);
            res.setVoid();
        } catch (QueueFullException e) {
            res.setException(e);
        }
    }

    @ActorAnn(name = "get", args = {})
    public void actor2(Result res, Object[] args) {
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
