package com.devexperts.dxlab.lincheck.tests;

/*
 * #%L
 * libtest
 * %%
 * Copyright (C) 2015 - 2017 Devexperts, LLC
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

import com.devexperts.dxlab.lincheck.LinChecker;
import com.devexperts.dxlab.lincheck.annotations.CTest;
import com.devexperts.dxlab.lincheck.annotations.Operation;
import com.devexperts.dxlab.lincheck.annotations.Reset;
import com.devexperts.dxlab.lincheck.libtest.State;
import org.junit.Test;

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