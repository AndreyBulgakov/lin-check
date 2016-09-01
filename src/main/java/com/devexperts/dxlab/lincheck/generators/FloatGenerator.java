package com.devexperts.dxlab.lincheck.generators;

import com.devexperts.dxlab.lincheck.util.ParameterizedGenerator;

/**
 * TODO add javadoc
 * TODO put parameters into constructor
 * begin, end, step
 */
public class FloatGenerator implements ParameterizedGenerator {
    private float begin = 0; // TODO -100
    private float end = 10; // TODO 100
    private float step = 1; // TODO 1 is default parameter for floating point number?

    public FloatGenerator() {
    }

    public Float[] generate() {
        int len = (int) ((end - begin) / step);
        Float[] x = new Float[len];
        for (int i = 0; i < len; i++) {
            x[i] = begin + step * i;
        }
        return x;
    }

    // TODO unused method
    public Float[] parseParameters(String parameters) {
        String[] s = parameters.split(":");
        Float[] floats = new Float[s.length];
        for (int i = 0; i < s.length; i++) {
            floats[i] = Float.parseFloat(s[i]);
        }
        return floats;
    }

    public void setParameters(String... parameters) {
        this.begin = Float.parseFloat(parameters[0]);
        this.end = Float.parseFloat(parameters[1]);
        this.step = Float.parseFloat(parameters[2]);
    }
}
