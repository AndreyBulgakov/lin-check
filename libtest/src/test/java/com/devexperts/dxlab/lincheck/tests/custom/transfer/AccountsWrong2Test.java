package com.devexperts.dxlab.lincheck.tests.custom.transfer;

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
