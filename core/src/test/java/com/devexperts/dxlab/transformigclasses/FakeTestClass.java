package com.devexperts.dxlab.transformigclasses;

import com.devexperts.dxlab.lincheck.Utils;
import com.devexperts.dxlab.lincheck.utils.FakeTestAbstract;
import com.devexperts.dxlab.transformigclasses.A;

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
