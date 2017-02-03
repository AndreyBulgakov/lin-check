package com.devexperts.dxlab.lincheck.generators;

import com.devexperts.dxlab.lincheck.ParameterGenerator;

import java.util.Random;

public class DoubleGen implements ParameterGenerator<Double> {
    private static final float DEFAULT_BEGIN = -10;
    private static final float DEFAULT_END = 10;
    private static final float DEFAULT_STEP = 0.1f;

    private final Random random = new Random();
    private final double begin;
    private final double end;
    private final double step;

    public DoubleGen(String configuration) {
        if (configuration.isEmpty()) { // use default configuration
            begin = DEFAULT_BEGIN;
            end = DEFAULT_END;
            step = DEFAULT_STEP;
            return;
        }
        String[] args = configuration.replaceAll("\\s", "").split(":");
        switch (args.length) {
        case 2: // begin:end
            begin = Double.parseDouble(args[0]);
            end = Double.parseDouble(args[1]);
            step = DEFAULT_STEP;
            break;
        case 3: // begin:step:end
            begin = Double.parseDouble(args[0]);
            step = Double.parseDouble(args[1]);
            end = Double.parseDouble(args[2]);
            break;
        default:
            throw new IllegalArgumentException("Configuration should have two (begin and end) " +
                "or three (begin, step and end) arguments  separated by comma");
        }
        if ((end - begin) / step >= Integer.MAX_VALUE)
            throw new IllegalArgumentException("step is too small for specified range");
    }

    public Double generate() {
        double delta = end - begin;
        if (step == 0) // step is not defined
            return begin + delta * random.nextDouble();
        int maxSteps = (int) (delta / step);
        return begin + delta * random.nextInt(maxSteps + 1);
    }
}
