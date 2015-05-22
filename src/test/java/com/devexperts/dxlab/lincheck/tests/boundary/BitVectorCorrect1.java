package com.devexperts.dxlab.lincheck.tests.boundary;

import com.devexperts.dxlab.lincheck.CheckerAnnotatedASM;
import com.devexperts.dxlab.lincheck.annotations.ActorAnn;
import com.devexperts.dxlab.lincheck.annotations.CTest;
import com.devexperts.dxlab.lincheck.annotations.Reload;
import com.devexperts.dxlab.lincheck.util.Result;
import org.cliffc.high_scale_lib.NonBlockingSetInt;
import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.assertTrue;


@CTest(iter = 200, actorsPerThread = {"1:3", "1:3"})
@CTest(iter = 200, actorsPerThread = {"1:3", "1:3", "1:3"})
public class BitVectorCorrect1 {
    public Set<Integer> q;

    @Reload
    public void reload() {
        q = new NonBlockingSetInt();
    }

    @ActorAnn(args = {"1:3"})
    public void add(Result res, Object[] args) throws Exception {
        Integer value = (Integer) args[0];
        res.setValue(q.add(value));
    }

    @ActorAnn(args = {"1:3"})
    public void remove(Result res, Object[] args) throws Exception {
        Integer value = (Integer) args[0];
        res.setValue(q.remove(value));
    }

    @ActorAnn(args = {})
    public void size(Result res, Object[] args) throws Exception {
        res.setValue(q.size());
    }

    @Test
    public void test() throws Exception {
        assertTrue(CheckerAnnotatedASM.check(new BitVectorCorrect1()));
        // TODO failed test
    }
}
