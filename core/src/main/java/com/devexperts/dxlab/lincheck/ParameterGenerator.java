package com.devexperts.dxlab.lincheck;

import com.devexperts.dxlab.lincheck.annotations.Operation;

/**
 * The implementation of this interface is used to generate parameters
 * for {@link Operation operations}.
  */
public interface ParameterGenerator<T> {
    T generate();

    final class Dummy implements ParameterGenerator<Object> {
        @Override
        public Object generate() {
            throw new UnsupportedOperationException();
        }
    }
}
