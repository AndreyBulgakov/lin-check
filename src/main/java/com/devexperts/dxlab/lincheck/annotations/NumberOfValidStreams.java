package com.devexperts.dxlab.lincheck.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * TODO remove it. Do not implement maximum parallel threads restriction under current issue
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface NumberOfValidStreams {
    int value();
}
