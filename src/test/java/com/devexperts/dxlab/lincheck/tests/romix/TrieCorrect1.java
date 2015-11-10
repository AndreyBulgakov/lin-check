package com.devexperts.dxlab.lincheck.tests.romix;

import com.devexperts.dxlab.lincheck.CheckerAnnotatedASM;
import com.devexperts.dxlab.lincheck.annotations.ActorAnn;
import com.devexperts.dxlab.lincheck.annotations.CTest;
import com.devexperts.dxlab.lincheck.annotations.Immutable;
import com.devexperts.dxlab.lincheck.annotations.Reload;
import com.devexperts.dxlab.lincheck.util.Result;
import com.romix.scala.collection.concurrent.TrieMap;
import org.junit.Test;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static junit.framework.TestCase.assertTrue;


@CTest(iter = 300, actorsPerThread = {"1:3", "1:3"})
@CTest(iter = 300, actorsPerThread = {"1:3", "1:3", "1:3"})
public class TrieCorrect1 {
    public Map<Integer, Integer> m;

    @Reload
    public void reload() {
        m = new TrieMap<>();
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
        assertTrue(CheckerAnnotatedASM.check(new TrieCorrect1()));
    }
}

