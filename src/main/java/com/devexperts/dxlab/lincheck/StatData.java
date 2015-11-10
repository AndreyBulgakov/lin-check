package com.devexperts.dxlab.lincheck;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class StatData {
    public static List<Integer> errorIters = new ArrayList<>();
    public static List<Integer> errorConcurIters = new ArrayList<>();
    public static List<Long> errorTimes = new ArrayList<>();


    public static void clear() {
        errorIters.clear();
        errorConcurIters.clear();
        errorTimes.clear();
    }

    public static void addIter(int v) {
        errorIters.add(v);
    }

    public static void addConcur(int v) {
        errorConcurIters.add(v);
    }

    public static void addTime(long v) {
        errorTimes.add(v);
    }

    public static void print(PrintWriter writer) {
        if (writer == null) {
            System.out.println("errorIters = " + errorIters);
            System.out.println("errorConcurIters = " + errorConcurIters);
            System.out.println("errorTimes = " + errorTimes);
        } else {
            writer.println("errorIters = " + errorIters);
            writer.println("errorConcurIters = " + errorConcurIters);
            writer.println("errorTimes = " + errorTimes);
        }
    }
}
