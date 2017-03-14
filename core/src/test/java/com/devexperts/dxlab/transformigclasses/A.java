package com.devexperts.dxlab.transformigclasses;

import com.devexperts.dxlab.lincheck.Utils;
import com.devexperts.dxlab.lincheck.utils.AParrent;

public class A extends AParrent{

    private B b = new B();
    private String securityString = "Security string";

    public void method1(){
        int a = 20;
        int b = 10;
        int c = a + b;
        int[] d = new int[10];
        d[1] = 40;
        Utils.consumeCPU(2);
    }

    public B getB() {
        return b;
    }

    public String getSecutityString() {
        return securityString;
    }
}
