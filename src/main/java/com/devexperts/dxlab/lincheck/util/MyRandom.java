package com.devexperts.dxlab.lincheck.util;

import java.util.Random;

public class MyRandom {
    private static final Random r = new Random(0);

    public static int fromInterval(Interval iv) {
        return r.nextInt(iv.to - iv.from) + iv.from;
    }

    public static int nextInt(int n) {
        return r.nextInt(n);
    }

    public static int nextInt() {
        return r.nextInt();
    }

    public static long nextLong() {
        return r.nextLong();
    }
}
