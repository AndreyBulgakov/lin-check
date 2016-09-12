package com.devexperts.dxlab.lincheck.generators;

import com.devexperts.dxlab.lincheck.Checker;

/**
 * Float numbers generator
 * Constructor parameters
 * <ul>
 *     <li>begin default value = -100</li>
 *     <li>end default value = 100</li>
 *     <li>step default value = 0.1</li>
 * </ul>
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
        return begin + Checker.r.nextInt((int) (end - begin)) + step * (int) (Checker.r.nextFloat() / step);
    }
}
