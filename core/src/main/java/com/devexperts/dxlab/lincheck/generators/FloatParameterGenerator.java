package com.devexperts.dxlab.lincheck.generators;

import com.devexperts.dxlab.lincheck.ParameterGenerator;

import java.util.Random;

public class FloatParameterGenerator implements ParameterGenerator<Float> {
    private static final float DEFAULT_BEGIN = -10;
    private static final float DEFAULT_END = 10;
    private static final float DEFAULT_STEP = 0.1f;

    private final Random random = new Random();
    private final float begin;
    private final float end;
    private final float step;

    public FloatParameterGenerator(String configuration) {
        if (configuration.isEmpty()) { // use default configuration
            begin = DEFAULT_BEGIN;
            end = DEFAULT_END;
            step = DEFAULT_STEP;
            return;
        }
        String[] args = configuration.replaceAll("\\s", "").split(":");
        switch (args.length) {
        case 2: // begin:end
            begin = Float.parseFloat(args[0]);
            end = Float.parseFloat(args[1]);
            step = DEFAULT_STEP;
            break;
        case 3: // begin:step:end
            begin = Float.parseFloat(args[0]);
            step = Float.parseFloat(args[1]);
            end = Float.parseFloat(args[2]);
            break;
        default:
            throw new IllegalArgumentException("Configuration should have two (begin and end) " +
                "or three (begin, step and end) arguments  separated by comma");
        }
    }

    public Float generate() {
        float delta = end - begin;
        if (step == 0) // step is not defined
            return begin + delta * random.nextFloat();
        int maxSteps = (int) (delta / step);
        return begin + delta * random.nextInt(maxSteps + 1);
    }
}
