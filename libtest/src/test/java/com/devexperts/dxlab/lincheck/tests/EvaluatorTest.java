package com.devexperts.dxlab.lincheck.tests;

import com.devexperts.dxlab.lincheck.LinChecker;
import com.devexperts.dxlab.lincheck.annotations.CTest;
import com.devexperts.dxlab.lincheck.annotations.Operation;
import com.devexperts.dxlab.lincheck.annotations.ReadOnly;
import com.devexperts.dxlab.lincheck.annotations.Reset;
import org.junit.Test;
import com.devexperts.dxlab.lincheck.libtest.Evaluator;

/**
 * Created by alexander on 08.02.17.
 */
@CTest(iterations = 30, actorsPerThread = {"1:3", "1:2"})
public class EvaluatorTest {

    private Evaluator evaluator;

    @Reset
    public void reload(){
        evaluator = new Evaluator(3);
    }

    @Operation
    public void add3(){
        evaluator.addThree2Times();
    }

    @Operation
    public void mult3(){
        evaluator.multThree();
    }

    @Operation
    @ReadOnly
    public int getVal(){
        return evaluator.getValue();
    }

    @Test
    public void test() {
        LinChecker.check(this);
    }
}