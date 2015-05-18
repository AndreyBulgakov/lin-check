package com.devexperts.dxlab.lincheck.tests.juc;

import com.devexperts.dxlab.lincheck.annotations.ActorAnn;
import com.devexperts.dxlab.lincheck.annotations.CTest;
import com.devexperts.dxlab.lincheck.annotations.Reload;
import com.devexperts.dxlab.lincheck.CheckerAnnotated;
import com.devexperts.dxlab.lincheck.util.Result;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;


@CTest(iter = 20, actorsPerThread = {"1:3", "1:3"})
@CTest(iter = 20, actorsPerThread = {"1:3", "1:3", "1:3"})
public class HashMapTest {
    public Map<Integer, Integer> m;

    @Reload
    public void reload() {
        m = new HashMap<>();
    }

    @ActorAnn(name = "put", args = {"1:4", "1:10"})
    public void actor1(Result res, Object[] args) {
        Integer key = (Integer) args[0];
        Integer value = (Integer) args[1];
        Integer prevValue = m.put(key, value);
        res.setValue(prevValue);
    }

    @ActorAnn(name = "get", args = {"1:4"})
    public void actor2(Result res, Object[] args) {
        Integer key = (Integer) args[0];
        Integer value = m.get(key);
        res.setValue(value);
    }

    public static void main(String[] args) throws InvocationTargetException, IllegalAccessException {
        CheckerAnnotated checker = new CheckerAnnotated();
        System.out.println(checker.checkAnnotated(new HashMapTest()));
    }
}

