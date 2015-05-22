package com.devexperts.dxlab.lincheck.tests.high_scale_lib;

import com.devexperts.dxlab.lincheck.CheckerAnnotatedASM;
import com.devexperts.dxlab.lincheck.annotations.ActorAnn;
import com.devexperts.dxlab.lincheck.annotations.CTest;
import com.devexperts.dxlab.lincheck.annotations.Reload;
import com.devexperts.dxlab.lincheck.util.Result;
import org.cliffc.high_scale_lib.old.NonBlockingHashSet;
import org.junit.Test;

import static org.junit.Assert.assertTrue;


@CTest(iter = 200, actorsPerThread = {"1:3", "1:3"})
@CTest(iter = 200, actorsPerThread = {"1:3", "1:3", "1:3"})
public class SetCorrect1 {
    public NonBlockingHashSet<Integer> q;

    @Reload
    public void reload() {
        q = new NonBlockingHashSet<>();
    }

    @ActorAnn(args = {"1:5"})
    public void add(Result res, Object[] args) throws Exception {
        Integer value = (Integer) args[0];
        res.setValue(q.add(value));
    }

    @ActorAnn(args = {"1:5"})
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
        assertTrue(CheckerAnnotatedASM.check(new SetCorrect1()));
        // TODO failed test

    }
}
