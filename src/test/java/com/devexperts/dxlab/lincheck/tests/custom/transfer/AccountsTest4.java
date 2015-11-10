package com.devexperts.dxlab.lincheck.tests.custom.transfer;

import com.devexperts.dxlab.lincheck.CheckerAnnotatedASM;
import com.devexperts.dxlab.lincheck.annotations.ActorAnn;
import com.devexperts.dxlab.lincheck.annotations.CTest;
import com.devexperts.dxlab.lincheck.annotations.Immutable;
import com.devexperts.dxlab.lincheck.annotations.Reload;
import com.devexperts.dxlab.lincheck.util.Result;
import org.junit.Test;

import static org.junit.Assert.assertFalse;

@CTest(iter = 300, actorsPerThread = {"1:3", "1:3"})
@CTest(iter = 300, actorsPerThread = {"1:3", "1:3", "1:3"})
public class AccountsTest4 {
    public Accounts acc;

    @Reload
    public void reload() {
        acc = new AccountsWrong3();
    }

    @Immutable
    @ActorAnn(args = {"1:4"})
    public void getAmount(Result res, Object[] args) {
        Integer id = (Integer) args[0];
        res.setValue(acc.getAmount(id));
    }

    @ActorAnn(args = {"1:4", "10:21"})
    public void setAmount(Result res, Object[] args) {
        Integer id = (Integer) args[0];
        Integer amount = (Integer) args[1];
        acc.setAmount(id, amount);
        res.setVoid();
    }

    @ActorAnn(args = {"1:4", "1:4", "1:10"})
    public void transfer(Result res, Object[] args) {
        Integer from = (Integer) args[0];
        Integer to = (Integer) args[1];
        Integer amount = (Integer) args[2];
        acc.transfer(from, to, amount);
        res.setVoid();
    }


    @Test
    public void test() throws Exception {
        assertFalse(CheckerAnnotatedASM.check(new AccountsTest4()));
    }
}
