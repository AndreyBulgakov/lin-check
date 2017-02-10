package com.devexperts.dxlab.lincheck.tests.ozertsov.state;

import com.devexperts.dxlab.lincheck.LinChecker;
import com.devexperts.dxlab.lincheck.annotations.CTest;
import com.devexperts.dxlab.lincheck.annotations.Operation;
import com.devexperts.dxlab.lincheck.annotations.Reset;
import org.junit.Test;
import ozertsov.signal.State;

/**
 * Created by alexander on 09.02.17.
 */
@CTest(iterations = 30, actorsPerThread = {"1:1"})
public class StateTest {

    private State state;

    @Reset
    public void reload(){
        state = new State(17);
    }

    @Operation
    public boolean run(){
        Thread t = new Thread(){
            @Override
            public void run() {
                writeY(state);
            }
        };
        t.start();
        for (int i = 0; i < state.N; i++)
        {
            synchronized (state)
            {
                if (state.signal == 1)
                    state.changes++;
                state.signal = 0;
            }
        }
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return (state.changes <= 6);
    }

    public static void writeY(Object o){
        State s = (State)o;
        for (int j = 0; j < s.N; j++){
            synchronized (s){
                if (s.signal == 0)
                    s.changes++;
                s.signal = 1;
            }
        }
    }

    @Test
    public void test() {
        LinChecker.check(this);
    }
}