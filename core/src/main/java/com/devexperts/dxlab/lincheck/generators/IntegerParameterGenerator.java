package com.devexperts.dxlab.lincheck.generators;

import com.devexperts.dxlab.lincheck.ParameterGenerator;

import java.util.Random;

public class IntegerParameterGenerator implements ParameterGenerator<Integer> {
    private static final int DEFAULT_BEGIN = -10;
    private static final int DEFAULT_END = 10;

    private final Random random = new Random();
    private final int begin;
    private final int end;

    public IntegerParameterGenerator(String configuration) {
        if (configuration.isEmpty()) { // use default configuration
            begin = DEFAULT_BEGIN;
            end = DEFAULT_END;
            return;
        }
        String[] args = configuration.replaceAll("\\s", "").split(":");
        switch (args.length) {
        case 2: // begin:end
            begin = Integer.parseInt(args[0]);
            end = Integer.parseInt(args[1]);
            break;
        default:
            throw new IllegalArgumentException("Configuration should have " +
                "two arguments (begin and end) separated by comma");
        }
    }

    public Integer generate() {
        return begin + random.nextInt(end - begin);
    }
}
