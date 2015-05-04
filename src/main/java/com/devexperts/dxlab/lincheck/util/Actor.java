package com.devexperts.dxlab.lincheck.util;

import java.util.Arrays;

public class Actor {
    public int ind;
    public int method;
    public Object[] args;
    public String methodName;

    public Actor(int ind, int method) {
        this.method = method;
        this.ind = ind;
    }

    public Actor(int ind, int method, Object... args) {
        this.ind = ind;
        this.method = method;
        this.args = args;
    }

    private static String argsToString(Object[] args) {
        if (args == null)
            return "null";

        int iMax = args.length - 1;
        if (iMax == -1)
            return "";

        StringBuilder b = new StringBuilder();
        for (int i = 0; ; i++) {
            b.append(String.valueOf(args[i]));
            if (i == iMax)
                return b.toString();
            b.append(", ");
        }
    }

    @Override
    public String toString() {
        return ind +
                "_" + methodName +
                "(" + argsToString(args) +
                ")";
    }
}
