package com.devexperts.dxlab.lincheck.util;

// TODO что это такое и зачем?
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
