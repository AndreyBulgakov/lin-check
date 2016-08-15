package com.devexperts.dxlab.lincheck.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by apykhtin on 8/12/2016.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface NumberOfValidStreams {
    int value();
}
