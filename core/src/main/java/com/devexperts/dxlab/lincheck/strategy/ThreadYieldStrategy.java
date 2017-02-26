package com.devexperts.dxlab.lincheck.strategy;


public class ThreadYieldStrategy implements Strategy {

    @Override
    public void onSharedVariableAccess(int location) {
        Thread.yield();
    }
}
