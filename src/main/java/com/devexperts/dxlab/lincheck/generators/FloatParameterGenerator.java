package com.devexperts.dxlab.lincheck.generators;

import com.devexperts.dxlab.lincheck.util.MyRandom;

import java.security.InvalidParameterException;

/**
 * Float numbers generator
 * begin, end, step - order constructor parameters
 */
public class FloatParameterGenerator implements ParameterGenerator {
    private float begin = -100;
    private float end = 100;
    private float step = 0.1F;

    public FloatParameterGenerator(String begin, String end, String step) {
        this.begin = Float.parseFloat(begin);
        this.end = Float.parseFloat(end);
        this.step = Float.parseFloat(step);
    }

    public FloatParameterGenerator(String begin, String end) {
        this.begin = Float.parseFloat(begin);
        this.end = Float.parseFloat(end);
    }

    public FloatParameterGenerator(String begin) {
        this.begin = Float.parseFloat(begin);
    }

    public FloatParameterGenerator() {
    }

    public Float generate() {
        return begin + MyRandom.nextInt((int) (end - begin)) + step * (int) (MyRandom.nextFloat() / step);
    }
}
