package main.java.com.devexperts.dxlab.lincheck.util;

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

    @Override
    public String toString() {
        return "Actor-" + ind +
                "{ " + methodName +
                "(" + Arrays.toString(args) +
                ") }";
    }
}
