package com.devexperts.dxlab.lincheck.annotations;

import org.omg.CORBA.Object;

import javax.lang.model.type.NullType;
import java.lang.annotation.Retention;
import java.lang.annotation.Repeatable;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotation for generate arguments for method
 * generator - class that implements generator interface. It generate parameters for methods
 * name - name for a set of parameters
 * generatorParameters - parameters for generator constructor
 */
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(Param.Params.class)
public @interface Param {
    Class generator() default Void.class;

    String name() default "";

    String[] generatorParameters() default {};

    /**
     * Holder annotation for {@link Param}.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @interface Params {
        Param[] value();
    }
}
