package com.devexperts.dxlab.lincheck.tests.amino_cbbs;

import com.devexperts.dxlab.lincheck.LinChecker;
import com.devexperts.dxlab.lincheck.annotations.CTest;
import com.devexperts.dxlab.lincheck.annotations.Operation;
import com.devexperts.dxlab.lincheck.annotations.Reset;
import org.junit.Test;
import amino_cbbs.Vector;

/**
 * Created by alexander on 10.02.17.
 */
@CTest(iterations = 30, actorsPerThread = {"1:2", "1:2"})
public class VectorTest {

    private Vector v1;
    private Vector v2;
    private Vector v3;

    @Reset
    public void reset() {
        v1 = new Vector(3, new int[]{1,2,3});
        v2 = new Vector(4, new int[]{1,2,3,4});
        v3 = new Vector(8, new int[]{1});
    }

    @Operation
    public int addv1Tov3() {
        v3.addAll(v1);
        return v3.getSize();
    }

    @Operation
    public int addv2Tov3() {
        v3.addAll(v2);
        return v3.getSize();
    }

    @Test(timeout = 1000000)
    public void test() {
        LinChecker.check(this);
    }
}
