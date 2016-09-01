package com.devexperts.dxlab.lincheck.util;

/**
 * TODO Use just java.lang.Object instead of this class. Class generator should know required types for method
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
