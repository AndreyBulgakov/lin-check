package main.java.com.devexperts.dxlab.lincheck;


import main.java.com.devexperts.dxlab.lincheck.queue.Queue;
import main.java.com.devexperts.dxlab.lincheck.queue.QueueEmptyException;
import main.java.com.devexperts.dxlab.lincheck.queue.QueueFullException;
import main.java.com.devexperts.dxlab.lincheck.queue.QueueWithoutAnySync;
import main.java.com.devexperts.dxlab.lincheck.util.*;

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
    public Result call(Actor act) {
        Result res = new Result();

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
        return res;
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
        Checker checker = new Checker();
        Caller caller = new QueueCaller(QueueWithoutAnySync.class);
        System.out.println(checker.check(caller));
    }
}
