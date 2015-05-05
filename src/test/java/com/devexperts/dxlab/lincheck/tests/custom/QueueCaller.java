package com.devexperts.dxlab.lincheck.tests.custom;


import com.devexperts.dxlab.lincheck.Checker;
import com.devexperts.dxlab.lincheck.tests.custom.queue.Queue;
import com.devexperts.dxlab.lincheck.tests.custom.queue.QueueEmptyException;
import com.devexperts.dxlab.lincheck.tests.custom.queue.QueueFullException;
import com.devexperts.dxlab.lincheck.tests.custom.queue.QueueWithoutAnySync;
import com.devexperts.dxlab.lincheck.util.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class QueueCaller implements Caller {
    Queue queue;

    Class objClass;

    public QueueCaller(Class objClass) {
        this.objClass = objClass;
        reload();
    }

    public void reload() {
        try {
            Constructor ctor = objClass.getConstructor(int.class);
            queue = (Queue) ctor.newInstance(10);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /*
        0 public int put(int x);
        1 public int get();
    */
    public void call(Actor act, Result res) {
        int method = act.method;
        Object[] args = act.args;

        if (method == 0) {
            Integer x = (Integer) args[0];
            try {
                queue.put(x);
                res.setVoid();
            } catch (QueueFullException e) {
                res.setException(e);
            }
        } else if (method == 1) {
            try {
                Integer value = queue.get();
                res.setValue(value);
            } catch (QueueEmptyException e) {
                res.setException(e);
            }
        }
    }

    public List<CheckerConfiguration> getConfigurations() {
        List<CheckerConfiguration> res = new ArrayList<>();

        ActorGenerator act1 = new ActorGenerator(0, "put", new Interval(1, 11));
        ActorGenerator act2 = new ActorGenerator(1, "get");

        res.add(new CheckerConfiguration()
                .setNumIterations(20)
                .addThread(new Interval(1, 3))
                .addThread(new Interval(1, 3))
                .addActorGenerator(act1)
                .addActorGenerator(act2)
        );

        res.add(new CheckerConfiguration()
                .setNumIterations(20)
                .addThread(new Interval(1, 3))
                .addThread(new Interval(1, 3))
                .addThread(new Interval(1, 3))
                .addActorGenerator(act1)
                .addActorGenerator(act2)
        );

        return res;
    }


    public static void main(String[] args) {
        long start = System.currentTimeMillis();

        Checker checker = new Checker();
        Caller caller = new QueueCaller(QueueWithoutAnySync.class);
        System.out.println(checker.check(caller));

        System.out.println(System.currentTimeMillis() - start);
    }
}
