package com.devexperts.dxlab.lincheck.tests.ozertsov.deadlock;

import com.devexperts.dxlab.lincheck.LinChecker;
import com.devexperts.dxlab.lincheck.annotations.CTest;
import com.devexperts.dxlab.lincheck.annotations.Operation;
import com.devexperts.dxlab.lincheck.annotations.Reset;
import org.junit.Test;
import com.devexperts.dxlab.lincheck.libtest.ozertsov.deadlock.DeadLock;

/**
 * Created by alexander on 09.02.17.
 */
@CTest(iterations = 30, actorsPerThread = {"1:2", "1:2"})
public class DeadLockTest {

    private DeadLock instance;

    @Reset
    public void reload(){
        instance = new DeadLock();
    }

    @Operation
    public int increment(){
        return instance.inc();
    }

    @Operation
    public int decrement(){
        return instance.dec();
    }

    @Operation
    public int incrementLoc1Lock2(){
        return  instance.parent();
    }

    @Operation
    public int incrementLoc2Lock1(){
        return  instance.child();
    }

    @Test(timeout = 1000000)
    public void test() {
        LinChecker.check(this);
    }
}