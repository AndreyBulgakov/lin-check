package com.devexperts.dxlab.lincheck.util;

public class Interval {
    public int from, to;

    public Interval(int from, int to) {
        this.from = from;
        this.to = to;
    }

    @Override
    public String toString() {
        return "[" + from +
                ", " + to +
                ')';
    }
}
