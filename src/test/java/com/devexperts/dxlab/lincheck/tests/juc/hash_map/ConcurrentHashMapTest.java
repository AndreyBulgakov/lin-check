package com.devexperts.dxlab.lincheck.tests.juc.hash_map;

import com.devexperts.dxlab.lincheck.CheckerAnnotatedASM;
import com.devexperts.dxlab.lincheck.annotations.ActorAnn;
import com.devexperts.dxlab.lincheck.annotations.CTest;
import com.devexperts.dxlab.lincheck.annotations.Immutable;
import com.devexperts.dxlab.lincheck.annotations.Reload;
import com.devexperts.dxlab.lincheck.CheckerAnnotated;
import com.devexperts.dxlab.lincheck.util.Result;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;


@CTest(iter = 200, actorsPerThread = {"1:3", "1:3"})
@CTest(iter = 200, actorsPerThread = {"1:3", "1:3", "1:3"})
public class ConcurrentHashMapTest {
    public Map<Integer, Integer> m;

    @Reload
    public void reload() {
        m = new ConcurrentHashMap<>();
    }

    @ActorAnn(args = {"1:4", "1:10"})
    public void put(Result res, Object[] args) throws Exception {
        Integer key = (Integer) args[0];
        Integer value = (Integer) args[1];
        res.setValue(m.put(key, value));
    }

    @Immutable
    @ActorAnn(args = {"1:4"})
    public void get(Result res, Object[] args) throws Exception {
        Integer key = (Integer) args[0];
        res.setValue(m.get(key));
    }

    @Test
    public void test() throws Exception {
        assertTrue(CheckerAnnotatedASM.check(new ConcurrentHashMapTest()));
    }
}

