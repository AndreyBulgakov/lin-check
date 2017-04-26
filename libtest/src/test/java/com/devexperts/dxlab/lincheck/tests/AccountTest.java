package com.devexperts.dxlab.lincheck.tests;

/*
 * #%L
 * libtest
 * %%
 * Copyright (C) 2015 - 2017 Devexperts, LLC
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

import com.devexperts.dxlab.lincheck.LinChecker;
import com.devexperts.dxlab.lincheck.annotations.*;
import com.devexperts.dxlab.lincheck.generators.IntGen;
import com.devexperts.dxlab.lincheck.libtest.Account;
import org.junit.Test;

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