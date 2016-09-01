package com.devexperts.dxlab.lincheck.tests.custom.setandget;

import com.devexperts.dxlab.lincheck.Checker;
import com.devexperts.dxlab.lincheck.generators.FloatGenerator;
import com.devexperts.dxlab.lincheck.annotations.*;
import com.devexperts.dxlab.lincheck.generators.IntegerGenerator;
import org.junit.Test;

import static org.junit.Assert.assertFalse;

/**
 * TODO javadoc
 */
@CTest(iter = 300, actorsPerThread = {"1:3", "1:3"})
@CTest(iter = 300, actorsPerThread = {"1:3", "1:3", "1:3"})
@Param(name = "key", clazz = FloatGenerator.class, opt = {"0", "10", "0.1"})
@Param(name = "value",  clazz = IntegerGenerator.class)
public class SetAndGetTest {
    public SetAndGet setAndGet;

    @Reset
    public void reload() {
        setAndGet = new SetAndGet();
    }

    @NumberOfValidStreams(2)
    @Operation(params = {"key"})
    public float setAndGet(float key) throws Exception{
        return setAndGet.setAndGet(key);
    }

    @Test
    public void test() throws Exception {
        Checker checker = new Checker();
        assertFalse(checker.checkAnnotated(new SetAndGetTest()));
    }

    private static class SetAndGet {
        private float c;

        public SetAndGet() {
            c = 0;
        }

        public float setAndGet(float c) {
            this.c = c;
            return this.c;
        }
    }
}

