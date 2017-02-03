package com.devexperts.dxlab.lincheck.annotations;

import com.devexperts.dxlab.lincheck.ParameterGenerator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER, ElementType.TYPE})
@Repeatable(Param.Params.class)
public @interface Param {
    String name() default "";
    Class<? extends ParameterGenerator<?>> gen() default ParameterGenerator.Dummy.class;
    String conf() default "";

    /**
     * Holder annotation for {@link Param}.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @interface Params {
        Param[] value();
    }
}