package com.devexperts.dxlab.lincheck.strategy;

/**
 * Created by andrey on 4/10/17.
 */

/**
 * This strategy invokes {@link Thread#yield()} method on every shared variable access.
 */
public class ThreadYieldStrategy implements Strategy {
    @Override
    public void onSharedVariableRead(int location) {
        Thread.yield();
    }

    @Override
    public void onSharedVariableWrite(int location) {
        Thread.yield();
    }
}
