package com.devexperts.dxlab.lincheck.tests.guava;

import com.devexperts.dxlab.lincheck.CheckerAnnotatedASM;
import com.devexperts.dxlab.lincheck.annotations.ActorAnn;
import com.devexperts.dxlab.lincheck.annotations.CTest;
import com.devexperts.dxlab.lincheck.annotations.Reload;
import com.devexperts.dxlab.lincheck.util.Result;
import com.google.common.collect.ConcurrentHashMultiset;
import org.junit.Test;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.Assert.assertTrue;


@CTest(iter = 300, actorsPerThread = {"1:3", "1:3"})
@CTest(iter = 300, actorsPerThread = {"1:3", "1:3", "1:3"})
public class SetCorrect1 {
    public Set<Integer> q;

    @Reload
    public void reload() {
        q = Collections.newSetFromMap(new ConcurrentHashMap<Integer, Boolean>());
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

    @Test
    public void test() throws Exception {
        assertTrue(CheckerAnnotatedASM.check(new SetCorrect1()));
    }
}
