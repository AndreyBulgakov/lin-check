package me.aevd.lintesting;


import me.aevd.lintesting.transfer.Accounts;
import me.aevd.lintesting.transfer.AccountsSynchronized;
import me.aevd.lintesting.util.Caller;
import me.aevd.lintesting.util.Result;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

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
    public Result call(int method, Object... args) {
        Result res = new Result();

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
}
