package com.devexperts.dxlab.lincheck.tests.ozertsov.evaluator;

import com.devexperts.dxlab.lincheck.LinChecker;
import com.devexperts.dxlab.lincheck.annotations.CTest;
import com.devexperts.dxlab.lincheck.annotations.Operation;
import com.devexperts.dxlab.lincheck.annotations.Reset;
import org.junit.Test;
import ozertsov.evaluator.Evaluator;

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
    public int add3(){
        evaluator.addThree2Times();
        return evaluator.getValue();
    }

    @Operation
    public int mult3(){
        evaluator.multThree();
        return evaluator.getValue();
    }

    @Test
    public void test() {
        LinChecker.check(this);
    }
}