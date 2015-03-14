package me.aevd.lintesting;


import me.aevd.lintesting.queue.Queue;
import me.aevd.lintesting.util.Actor;
import me.aevd.lintesting.util.Caller;
import me.aevd.lintesting.util.Result;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Random;

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
    public Result call(int method, Object... args) {
        Result res = new Result();

        if (method == 0) {
            Integer x = (Integer) args[0];
            Integer value = queue.put(x);
            res.setValue(value);
        } else if (method == 1) {
            Integer value = queue.get();
            res.setValue(value);
        }
        return res;
    }


    public Actor[][] generateActors(int numThreads) {
        Random random = new Random();

        Actor[][] actors = new Actor[numThreads][];
        int ind = 0;
        for (int i = 0; i < numThreads; i++) {
            int cnt = random.nextInt(2) + 1;
            actors[i] = new Actor[cnt];
            for (int j = 0; j < cnt; j++) {
                int t = random.nextInt(2);
                if (t == 0) {
                    actors[i][j] = new Actor(ind++, 0, random.nextInt(10) + 1);
                    actors[i][j].methodName = "put";
                } else if (t == 1) {
                    actors[i][j] = new Actor(ind++, 1);
                    actors[i][j].methodName = "get";
                }
            }
        }

        return actors;
    }
}
