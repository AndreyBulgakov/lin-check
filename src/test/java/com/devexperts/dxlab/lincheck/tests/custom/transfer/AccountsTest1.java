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
import com.devexperts.dxlab.lincheck.util.Result;
import org.junit.Test;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;

@CTest(iter = 300, actorsPerThread = {"1:3", "1:3"})
@CTest(iter = 300, actorsPerThread = {"1:3", "1:3", "1:3"})
public class AccountsTest1 {
    public Accounts acc;

    @Reload
    public void reload() {
        acc = new AccountsWrong5();
    }

    @ReadOnly
    @Operation(args = {"1:4"})
    public void getAmount(Result res, Object[] args) {
        Integer id = (Integer) args[0];
        res.setValue(acc.getAmount(id));
    }

    @Operation(args = {"1:4", "10:21"})
    public void setAmount(Result res, Object[] args) {
        Integer id = (Integer) args[0];
        Integer amount = (Integer) args[1];
        acc.setAmount(id, amount);
        res.setVoid();
    }

    @Operation(args = {"1:4", "1:4", "1:10"})
    public void transfer(Result res, Object[] args) {
        Integer from = (Integer) args[0];
        Integer to = (Integer) args[1];
        Integer amount = (Integer) args[2];
        acc.transfer(from, to, amount);
        res.setVoid();
    }


    @Test
    public void test() throws Exception {
        assertFalse(Checker.check(new AccountsTest1()));
    }
}
