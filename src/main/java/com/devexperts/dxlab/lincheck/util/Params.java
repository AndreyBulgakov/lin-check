package com.devexperts.dxlab.lincheck.util;

/**
 * Created by apykhtin on 8/4/2016.
 */
public class Params {
    public Object[] objects;
    public int from, to;
    String type;

    public Params(Object[] objects, String type) {
        this.objects = objects;
        this.from = 0;
        this.to = objects.length;
        this.type = type;
    }

    @Override
    public String toString() {
        return "[" + from +
                ", " + to +
                ')';
    }

}
