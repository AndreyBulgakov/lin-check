package com.devexperts.dxlab.lincheck.libtest.ozertsov.set;

/**
 * Created by alexander on 18.02.17.
 */
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation which indicates how method should be used inside multi-threaded
 * program.
 *
 * @author ganzhi
 *
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.METHOD)
public @interface Parallel {
    /**
     * @return
     */
    ParallelType value();
}