package com.devexperts.dxlab.lincheck;

public class Utils {
    private static volatile int consumedCPU = (int) System.currentTimeMillis();

    public static void consumeCPU(int tokens) {
        int t = consumedCPU; // volatile read
        for (int i = tokens; i > 0; i--)
            t += (t * 0x5DEECE66DL + 0xBL + i) & (0xFFFFFFFFFFFFL);
        if (t == 42)
            consumedCPU += t;
    }
}
