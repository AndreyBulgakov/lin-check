package me.aevd.lintesting;


import me.aevd.lintesting.queue.Queue;
import me.aevd.lintesting.queue.QueueEmptyException;
import me.aevd.lintesting.queue.QueueFullException;
import me.aevd.lintesting.queue.QueueWithoutAnySync;
import me.aevd.lintesting.util.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

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

    public CheckerConfiguration getConfiguration() {
        ActorGenerator[] actors = new ActorGenerator[]{
                new ActorGenerator(0, "put", new Interval[]{new Interval(1, 11)}),
                new ActorGenerator(1, "get", new Interval[0])
        };

        CheckerConfiguration conf = new CheckerConfiguration()
                .setNumIterations(2)
                .addThread(new Interval(1, 3))
                .addThread(new Interval(1, 3))
                .addActorGenerator(actors[0])
                .addActorGenerator(actors[1]);

        return conf;
    }

    public static void main(String[] args) {
        Checker checker = new Checker();
        Caller caller = new QueueCaller(QueueWithoutAnySync.class);
        System.out.println(checker.check(caller));
    }
}
