package com.devexperts.dxlab.lincheck.tests.guava;

import com.devexperts.dxlab.lincheck.CheckerAnnotatedASM;
import com.devexperts.dxlab.lincheck.annotations.ActorAnn;
import com.devexperts.dxlab.lincheck.annotations.CTest;
import com.devexperts.dxlab.lincheck.annotations.Reload;
import com.devexperts.dxlab.lincheck.util.Result;
import com.google.common.collect.ConcurrentHashMultiset;
import org.junit.Test;

import static org.junit.Assert.assertTrue;



@CTest(iter = 200, actorsPerThread = {"1:3", "1:3"})
@CTest(iter = 200, actorsPerThread = {"1:3", "1:3", "1:3"})
public class MultisetCorrect1 {
    public ConcurrentHashMultiset<Integer> q;

    @Reload
    public void reload() {
        q = ConcurrentHashMultiset.create();
    }

    @ActorAnn(args = {"1:4", "1:3"})
    public void add(Result res, Object[] args) throws Exception {
        Integer value = (Integer) args[0];
        Integer count = (Integer) args[1];
        res.setValue(q.add(value, count));
    }

    @ActorAnn(args = {"1:4", "1:3"})
    public void remove(Result res, Object[] args) throws Exception {
        Integer value = (Integer) args[0];
        Integer count = (Integer) args[1];
        res.setValue(q.remove(value, count));
    }

    @Test
    public void test() throws Exception {
        assertTrue(CheckerAnnotatedASM.check(new MultisetCorrect1()));
    }
}
