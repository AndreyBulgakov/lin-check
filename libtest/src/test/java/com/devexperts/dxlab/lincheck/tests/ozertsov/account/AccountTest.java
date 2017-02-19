package com.devexperts.dxlab.lincheck.tests.ozertsov.account;

import com.devexperts.dxlab.lincheck.LinChecker;
import com.devexperts.dxlab.lincheck.annotations.CTest;
import com.devexperts.dxlab.lincheck.annotations.Operation;
import com.devexperts.dxlab.lincheck.annotations.Param;
import com.devexperts.dxlab.lincheck.annotations.Reset;
import com.devexperts.dxlab.lincheck.generators.IntGen;
import org.junit.Test;
import com.devexperts.dxlab.lincheck.libtest.ozertsov.account.Account;

/**
 * Created by alexander on 09.02.17.
 */
@CTest(iterations = 30, actorsPerThread = {"1:2", "1:2"})
@Param(name = "value", gen = IntGen.class)
public class AccountTest {

    private Account account;

    @Reset
    public void reload(){
        account = new Account();
    }

    @Operation(params = {"value"})
    public int withdraw(int value){
        account.withdraw(value);
        return account.read();
    }

    @Operation(params = {"value"})
    public int deposit(int value){
        account.deposit(value);
        return account.read();
    }

    @Test
    public void test() {
        LinChecker.check(this);
    }
}