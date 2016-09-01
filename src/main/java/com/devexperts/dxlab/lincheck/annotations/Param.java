package com.devexperts.dxlab.lincheck.annotations;

import org.omg.CORBA.Object;

import java.lang.annotation.Retention;
import java.lang.annotation.Repeatable;
import java.lang.annotation.RetentionPolicy;

/**
 * TODO add javadoc
 */
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(Param.Params.class)
public @interface Param {
    Class clazz() default String.class; // TODO String??
    String name() default ""; // TODO null by default
    String[] opt() default {};

    /**
     * Holder annotation for {@link Param}.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @interface Params {
        Param[] value();
    }
}
