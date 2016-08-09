package com.devexperts.dxlab.lincheck.SimpleGenerators;


import com.devexperts.dxlab.lincheck.util.ParameterizedGenerator;

/**
 * Created by apykhtin on 8/4/2016.
 */
public class IntegerGenerator implements ParameterizedGenerator {
    private int begin = 0;
    private int end = 10;
    public IntegerGenerator(){}
    public Integer[] generate(){
        Integer[] x = new Integer[end];
        for (int i = 0; i < end; i++) {
            x[i] = i;
        }
        return x;
    }

    public void setParameters(String... parameters){
        this.begin = Integer.parseInt(parameters[0]);
        this.end = Integer.parseInt(parameters[1]);
    }
}
