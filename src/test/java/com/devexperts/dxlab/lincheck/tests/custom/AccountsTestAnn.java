package com.devexperts.dxlab.lincheck.tests.custom;

import com.devexperts.dxlab.lincheck.CheckerAnnotated;
import com.devexperts.dxlab.lincheck.annotations.ActorAnn;
import com.devexperts.dxlab.lincheck.annotations.CTest;
import com.devexperts.dxlab.lincheck.annotations.Reload;
import com.devexperts.dxlab.lincheck.tests.custom.counter.Counter;
import com.devexperts.dxlab.lincheck.tests.custom.counter.CounterWithoutAnySync;
import com.devexperts.dxlab.lincheck.tests.custom.transfer.Accounts;
import com.devexperts.dxlab.lincheck.tests.custom.transfer.AccountsSynchronized;
import com.devexperts.dxlab.lincheck.util.Result;

import java.lang.reflect.InvocationTargetException;

@CTest(iter = 1, actorsPerThread = {"1:3", "1:3"})
@CTest(iter = 1, actorsPerThread = {"1:3", "1:3", "1:3"})
public class AccountsTestAnn {
    public Accounts acc;

    @Reload
    public void reload() {
        acc = new AccountsSynchronized();
    }

    @ActorAnn(name = "getAmount", args = {"1:4"})
    public void getAmount(Result res, Object[] args) {
        Integer id = (Integer) args[0];
        Integer amount = acc.getAmount(id);
        res.setValue(amount);
    }

    @ActorAnn(name = "setAmount", args = {"1:4", "10:21"})
    public void setAmount(Result res, Object[] args) {
        Integer id = (Integer) args[0];
        Integer amount = (Integer) args[1];
        acc.setAmount(id, amount);
        res.setVoid();
    }

    @ActorAnn(name = "transfer", args = {"1:4", "1:4", "1:10"})
    public void transfer(Result res, Object[] args) {
        Integer from = (Integer) args[0];
        Integer to = (Integer) args[1];
        Integer amount = (Integer) args[2];
        acc.transfer(from, to, amount);
        res.setVoid();
    }



    public static void main(String[] args) throws InvocationTargetException, IllegalAccessException {
        CheckerAnnotated checker = new CheckerAnnotated();
        AccountsTestAnn c = new AccountsTestAnn();
        boolean result = checker.checkAnnotated(c);
        System.out.println(c.acc.getClass().getSimpleName() + " " + (result ? "error not found" : "error found"));
    }
}
