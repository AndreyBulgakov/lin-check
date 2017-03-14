package com.devexperts.dxlab.lincheck.tests;

import com.devexperts.dxlab.lincheck.LinChecker;
import com.devexperts.dxlab.lincheck.annotations.*;
import com.devexperts.dxlab.lincheck.generators.IntGen;
import org.junit.Test;
import com.devexperts.dxlab.lincheck.libtest.Account;

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
    public void withdraw(int value){
        account.withdraw(value);
    }

    @Operation(params = {"value"})
    public void deposit(int value){
        account.deposit(value);
    }

    @Operation
    @ReadOnly
    public int result(){
        return account.read();
    }

    @Test
    public void test() {
        LinChecker.check(this);
    }
}