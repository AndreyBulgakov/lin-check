package com.devexperts.dxlab.lincheck.strategy;

/**
 * Strategy that call Thread.yield in each onSharedVariableAccess method call
 */
public class ThreadYieldStrategy implements Strategy {

    @Override
    public void onSharedVariableAccess(int location) {
        Thread.yield();
    }
}
