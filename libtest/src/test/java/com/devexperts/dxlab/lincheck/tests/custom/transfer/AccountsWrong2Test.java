package com.devexperts.dxlab.lincheck.tests.custom.transfer;

import com.devexperts.dxlab.lincheck.LinChecker;
import com.devexperts.dxlab.lincheck.annotations.*;
import com.devexperts.dxlab.lincheck.generators.IntGen;
import com.devexperts.dxlab.lincheck.libtest.custom.transfer.Accounts;
import com.devexperts.dxlab.lincheck.libtest.custom.transfer.AccountsWrong2;
import org.junit.Test;

/**
 * Created by alexander on 26.02.17.
 */
@CTest(iterations = 500, actorsPerThread = {"2:5", "2:5"})
@Param(name = "id", gen = IntGen.class, conf = "1:2")
@Param(name = "amount", gen = IntGen.class)
public class AccountsWrong2Test {
    private Accounts acc;

    @Reset
    public void reload() throws Exception {
        acc = new AccountsWrong2();
    }

    @ReadOnly
    @Operation(params = {"id"})
    public int getAmount(int key) {
        return acc.getAmount(key);
    }

    @Operation(params = {"id", "amount"})
    public void setAmount(int key, int value) {
        acc.setAmount(key, value);
    }

    @Operation
    public void transfer(@Param(name = "id") int from, @Param(name = "id") int to, @Param(name = "amount") int amount) {
        acc.transfer(from, to, amount);
    }

    @Test(expected = AssertionError.class)
    public void test() throws Exception {
        LinChecker.check(this);
    }
}
