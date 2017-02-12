package com.devexperts.dxlab.lincheck.tests.counter;

import com.devexperts.dxlab.lincheck.LinChecker;
import com.devexperts.dxlab.lincheck.annotations.CTest;
import com.devexperts.dxlab.lincheck.annotations.Operation;
import com.devexperts.dxlab.lincheck.annotations.Reset;
import counter.AtomicCorrectCounter;
import counter.Counter;
import counter.SynchonizedCorrectCounter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.List;

/**
 * Created by alexander on 12.02.17.
 */
@CTest(iterations = 50, actorsPerThread = {"2:5", "2:5"})
@RunWith(Parameterized.class)
public class CorrectCounterTests {
    private final Class<? extends Counter> counterClass;
    private Counter counter;

    @Parameterized.Parameters
    public static List<Object[]> params() {
        return Arrays.<Object[]>asList(
                new Object[] {AtomicCorrectCounter.class},
                new Object[] {SynchonizedCorrectCounter.class}
        );
    }

    public CorrectCounterTests(Class<? extends Counter> accountsClass) {
        this.counterClass = accountsClass;
    }

    @Reset
    public void reload() throws Exception {
        counter = counterClass.newInstance();
    }

    @Operation
    public int incAndGet() {
        return counter.incrementAndGet();
    }

    @Test
    public void test() throws Exception {
        LinChecker.check(this);
    }
}
