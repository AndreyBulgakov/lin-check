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
import com.devexperts.dxlab.lincheck.annotations.CTest;
import com.devexperts.dxlab.lincheck.annotations.Operation;
import com.devexperts.dxlab.lincheck.annotations.ReadOnly;
import com.devexperts.dxlab.lincheck.annotations.Reset;
import com.devexperts.dxlab.lincheck.libtest.Vector;
import org.junit.Test;

/**
 * Created by alexander on 10.02.17.
 */
@CTest(iterations = 60, actorsPerThread = {"1:2", "1:2"})
public class VectorTest {

    private Vector v1;
    private Vector v2;
    private Vector v3;

    @Reset
    public void reset() {
        v1 = new Vector(3, new int[]{1,2,3});
        v2 = new Vector(4, new int[]{1,2,3,4});
        v3 = new Vector(8, new int[]{1});
    }

    @Operation
    public void addv1Tov3() {
        v3.addAll(v1);
    }

    @Operation
    public void addv2Tov3() {
        v3.addAll(v2);
    }

    @Operation
    @ReadOnly
    public int size(){
        return v3.getSize();
    }

    @Operation
    @ReadOnly
    public int length(){
        return v3.getLength();
    }

    @Test(timeout = 1000000)
    public void test() {
        LinChecker.check(this);
    }
}
