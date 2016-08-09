package com.devexperts.dxlab.lincheck.util;

/**
 * Created by apykhtin on 8/4/2016.
 */
public class Params {
    public Object[] objects;
    public int from, to;

    public Params(Object[] objects) {
        this.objects = objects;
        this.from = 0;
        this.to = objects.length;
    }

    @Override
    public String toString() {
        return "[" + from +
                ", " + to +
                ')';
    }

}
