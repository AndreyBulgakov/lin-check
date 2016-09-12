package com.devexperts.dxlab.lincheck.generators;


import com.devexperts.dxlab.lincheck.util.MyRandom;

import java.security.InvalidParameterException;
/**
 * Float numbers generator
 * begin, end - order constructor parameters
 */
public class IntegerParameterGenerator implements ParameterGenerator {
    private int begin = 0;
    private int end = 10;

    public IntegerParameterGenerator(String begin, String end){
        this.begin = Integer.parseInt(begin);
        this.end = Integer.parseInt(end);
    }
    public IntegerParameterGenerator(String begin){
        this.begin = Integer.parseInt(begin);
    }
    public IntegerParameterGenerator(){
    }

    public Integer generate() {
        return begin + MyRandom.nextInt(end - begin);
    }
}
