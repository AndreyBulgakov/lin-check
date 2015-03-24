package me.aevd.lintesting;


import me.aevd.lintesting.transfer.Accounts;
import me.aevd.lintesting.transfer.AccountsSynchronized;
import me.aevd.lintesting.util.Actor;
import me.aevd.lintesting.util.Caller;
import me.aevd.lintesting.util.CheckerConfiguration;
import me.aevd.lintesting.util.Result;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Random;

public class AccountsCaller implements Caller {
    Accounts accounts;

    Class objClass;

    public AccountsCaller(Class objClass) {
        this.objClass = objClass;
        reload();
    }

    public void reload() {
        try {
            Constructor ctor = objClass.getConstructor();
            accounts = (Accounts) ctor.newInstance();
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
        0 - getAmount(id)
        1 - setAmount(id, value)
        2 - transfer(from, to, value)
    */
    public Result call(Actor act) {
        Result res = new Result();

        int method = act.method;
        Object[] args = act.args;

        if (method == 0) {
            Integer id = (Integer) args[0];
            Integer value = accounts.getAmount(id);
            res.setValue(value);
        } else if (method == 1) {
            Integer id = (Integer) args[0];
            Integer value = (Integer) args[1];
            accounts.setAmount(id, value);
            res.setVoid();
        } else if (method == 2) {
            Integer from = (Integer) args[0];
            Integer to = (Integer) args[1];
            Integer value = (Integer) args[2];
            accounts.transfer(from, to, value);
            res.setVoid();
        }
        return res;
    }

    @Override
    public CheckerConfiguration getConfiguration() {
        return null; // TODO fix
    }


    public Actor[][] generateActors(int numThreads) {
        Random random = new Random();

        Actor[][] actors = new Actor[numThreads][];
        int ind = 0;
        for (int i = 0; i < numThreads; i++) {
            int cnt = random.nextInt(2) + 1;
            actors[i] = new Actor[cnt];
            for (int j = 0; j < cnt; j++) {
                int t = random.nextInt(3);
                if (t == 0) {
                    actors[i][j] = new Actor(ind++, 0, random.nextInt(2));
                    actors[i][j].methodName = "get";
                } else if (t == 1) {
                    actors[i][j] = new Actor(ind++, 1, random.nextInt(2), random.nextInt(10));
                    actors[i][j].methodName = "set";
                } else if (t == 2) {
                    int from = -1;
                    int to = -1;

                    while (from == to) {
                        from = random.nextInt(2);
                        to = random.nextInt(2);
                    }

                    actors[i][j] = new Actor(ind++, 2, from, to, random.nextInt(10));
                    actors[i][j].methodName = "transfer";
                }
            }
        }

        return actors;
    }
}
