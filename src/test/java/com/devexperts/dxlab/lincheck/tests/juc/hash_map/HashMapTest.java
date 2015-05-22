package com.devexperts.dxlab.lincheck.tests.juc.hash_map;

import com.devexperts.dxlab.lincheck.CheckerAnnotatedASM;
import com.devexperts.dxlab.lincheck.annotations.ActorAnn;
import com.devexperts.dxlab.lincheck.annotations.CTest;
import com.devexperts.dxlab.lincheck.annotations.Immutable;
import com.devexperts.dxlab.lincheck.annotations.Reload;
import com.devexperts.dxlab.lincheck.CheckerAnnotated;
import com.devexperts.dxlab.lincheck.util.Result;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


@CTest(iter = 200, actorsPerThread = {"1:3", "1:3"})
@CTest(iter = 200, actorsPerThread = {"1:3", "1:3", "1:3"})
public class HashMapTest {
    public Map<Integer, Integer> m;

    @Reload
    public void reload() {
        m = new HashMap<>();
    }

    @ActorAnn(args = {"1:4", "1:10"})
    public void put(Result res, Object[] args) throws Exception {
        Integer key = (Integer) args[0];
        Integer value = (Integer) args[1];
        Integer prevValue = m.put(key, value);
        res.setValue(prevValue);
    }

    @Immutable
    @ActorAnn(args = {"1:4"})
    public void get(Result res, Object[] args) throws Exception {
        Integer key = (Integer) args[0];
        Integer value = m.get(key);
        res.setValue(value);
    }

    @Test
    public void test() throws Exception {
        CheckerAnnotatedASM checker = new CheckerAnnotatedASM();
        assertFalse(checker.checkAnnotated(new HashMapTest()));
    }
}

