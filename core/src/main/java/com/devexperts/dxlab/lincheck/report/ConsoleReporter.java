package com.devexperts.dxlab.lincheck.report;

import java.io.PrintStream;

/**
 * Created by alexander on 09.02.17.
 */
public class ConsoleReporter extends Reporter {

    public ConsoleReporter(int iterations, int invokations) {
        super(iterations, invokations, new PrintStream(System.out));
    }
}
