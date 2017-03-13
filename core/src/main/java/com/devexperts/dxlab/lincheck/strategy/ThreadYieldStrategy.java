package com.devexperts.dxlab.lincheck.strategy;

/**
 * This strategy invokes {@link Thread#yield()} method on every shared variable access.
 */
public class ThreadYieldStrategy implements Strategy {

    @Override
    public void onSharedVariableAccess(int location) {
        Thread.yield();
    }
}
