package com.devexperts.dxlab.lincheck.generators;


import com.devexperts.dxlab.lincheck.Checker;

/**
 * Float numbers generator
 * Constructor parameters
 * <ul>
 *     <li><b>begin</b> default value = -10</li>
 *     <li><b>end</b> default value = 10</li>
 * </ul>
 */
public class IntegerParameterGenerator implements ParameterGenerator {
    private int begin = -10;
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
        return begin + Checker.r.nextInt(end - begin);
    }
}
