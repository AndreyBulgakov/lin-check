package com.devexperts.dxlab.lincheck.tests;

import com.devexperts.dxlab.lincheck.LinChecker;
import com.devexperts.dxlab.lincheck.annotations.CTest;
import com.devexperts.dxlab.lincheck.annotations.Operation;
import com.devexperts.dxlab.lincheck.annotations.Reset;
import org.junit.Test;
import com.devexperts.dxlab.lincheck.libtest.Race;

/**
 * Created by alexander on 09.02.17.
 */
@CTest(iterations = 30, actorsPerThread = {"1:2", "1:2"})
public class RaceTest {

    private Race race;

    @Reset
    public void reload(){
        race = new Race();
    }

    @Operation
    public int dec(){
        return race.dec();
    }

    @Operation
    public int inc(){
        return race.add2();
    }

    @Test(timeout = 1000000)
    public void test() {
        LinChecker.check(this);
    }
}
