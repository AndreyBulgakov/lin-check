package com.devexperts.dxlab.lincheck.tests.custom.setandget;

import com.devexperts.dxlab.lincheck.Checker;
import com.devexperts.dxlab.lincheck.SimpleGenerators.FloatGenerator;
import com.devexperts.dxlab.lincheck.annotations.*;
import com.devexperts.dxlab.lincheck.util.Generator;
import com.devexperts.dxlab.lincheck.SimpleGenerators.IntegerGenerator;
import org.junit.Test;
import thesis_example.SetAndGet;

import static org.junit.Assert.assertFalse;

/**
 * Created by apykhtin on 7/29/2016.
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
    @NumberOfValidStreams(1)
    @Operation
    public int return3() throws Exception{
        return 3;
    }

    @Test
    public void test() throws Exception {
        Checker checker = new Checker();
        assertFalse(checker.checkAnnotated(new SetAndGetTest()));
    }
}
class DoubleGeneratot implements Generator{
    public Double[] generate(){
        Double[] a = {1.0, 2.0, 3.0, 4.0};
        return a;
    }
}