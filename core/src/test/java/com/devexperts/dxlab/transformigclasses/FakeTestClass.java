package com.devexperts.dxlab.transformigclasses;

/*
 * #%L
 * core
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

import com.devexperts.dxlab.lincheck.utils.FakeTestAbstract;

/**
 * Created by andrey on 2/20/17.
 */

public class FakeTestClass extends FakeTestAbstract {
    A a = new A();
    private String securityString = "Security string";

    public void method1(){
        int a = 20;
        String s = this.a.getSecutityString();
        int b = 10;
        int c = a + b;
        int[] d = new int[10];
        d[1] = 40;
    }

    public String getString() {
        return securityString;
    }

    @Override
    public A getA() {
        return a;
    }

    @Override
    public void invoke() {
        method1();
    }
}
