/*
 *  Lincheck - Linearizability checker
 *  Copyright (C) 2015 Devexperts LLC
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.devexperts.dxlab.lincheck.tests.custom.transfer;

import com.devexperts.dxlab.lincheck.Checker;
import com.devexperts.dxlab.lincheck.annotations.*;
import com.devexperts.dxlab.lincheck.annotations.ReadOnly;
import com.devexperts.dxlab.lincheck.generators.IntegerParameterGenerator;
import librariesForTesting.tests.custom.transfer.Accounts;
import librariesForTesting.tests.custom.transfer.AccountsWrong5;
import org.junit.Test;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;

@CTest(iter = 300, actorsPerThread = {"1:10", "1:10"})
@CTest(iter = 300, actorsPerThread = {"1:10", "1:10", "1:10"})
@Param(name = "id", generator = IntegerParameterGenerator.class)
@Param(name = "amount", generator = IntegerParameterGenerator.class)
public class AccountsTest1 {
    public Accounts acc;

    @Reset
    public void reload() {
        acc = new AccountsWrong5();
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
    public void transfer(@Param(name = "id") int from, @Param(generator = IntegerParameterGenerator.class) int to, @Param(generator = IntegerParameterGenerator.class) int amount) {
        acc.transfer(from, to, amount);
    }


    @Test
    public void test() throws Exception {
        assertFalse(Checker.check(new AccountsTest1()));
    }
}
