package com.devexperts.dxlab.lincheck.strategy;

// TODO javadoc
public class ThreadYieldStrategy implements Strategy {

    @Override
    public void onSharedVariableAccess(int location) {
        Thread.yield();
    }
}
