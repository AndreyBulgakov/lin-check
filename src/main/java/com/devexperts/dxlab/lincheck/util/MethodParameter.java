package com.devexperts.dxlab.lincheck.util;

/**
 * Created by apykhtin on 8/1/2016.
 */
public class MethodParameter {
    public String type;
    public Object value;
    public MethodParameter(String type, Object value){
        this.type = type;
        this.value = value;
    }
    @Override
    public String toString() {
        return value.toString();
    }
}
