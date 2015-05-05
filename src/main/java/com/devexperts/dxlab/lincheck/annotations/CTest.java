package com.devexperts.dxlab.lincheck.annotations;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@Repeatable(CTest.CTests.class)
public @interface CTest {
    int iter();
    String[] actorsPerThread();

    /**
     * Holder annotation for {@link CTest}.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @interface CTests {
        CTest[] value();
    }
}
