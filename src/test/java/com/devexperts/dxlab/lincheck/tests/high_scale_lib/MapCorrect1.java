package com.devexperts.dxlab.lincheck.tests.high_scale_lib;

import com.devexperts.dxlab.lincheck.CheckerAnnotatedASM;
import com.devexperts.dxlab.lincheck.annotations.ActorAnn;
import com.devexperts.dxlab.lincheck.annotations.CTest;
import com.devexperts.dxlab.lincheck.annotations.Reload;
import com.devexperts.dxlab.lincheck.util.Result;
import org.cliffc.high_scale_lib.old.NonBlockingHashMap;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertTrue;


//@CTest(iter = 300, actorsPerThread = {"1:3", "1:3"})
@CTest(iter = 300, actorsPerThread = {"1:3", "1:3", "1:3"})
public class MapCorrect1 {
    public Map<Integer, Integer> q;

    @Reload
    public void reload() {
        q = new NonBlockingHashMap<>();
    }

    @ActorAnn(args = {"1:3", "1:10"})
    public void put(Result res, Object[] args) throws Exception {
        Integer key = (Integer) args[0];
        Integer value = (Integer) args[1];
        res.setValue(q.put(key, value));
    }

    @ActorAnn(args = {"1:3"})
    public void get(Result res, Object[] args) throws Exception {
        Integer key = (Integer) args[0];
        res.setValue(q.get(key));
    }

//    @ActorAnn(args = {})
//    public void size(Result res, Object[] args) throws Exception {
//        res.setValue(q.size());
//    }

    @Test
    public void test() throws Exception {
        assertTrue(CheckerAnnotatedASM.check(new MapCorrect1()));
    }
}
