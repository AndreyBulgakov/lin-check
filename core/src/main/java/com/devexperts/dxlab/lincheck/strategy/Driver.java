package com.devexperts.dxlab.lincheck.strategy;


import co.paralleluniverse.fibers.Suspendable;

public interface Driver {
    @Suspendable
    void switchThread(int targetThreadId);

    @Suspendable
    void switchOnEndOfThread(int targetThreadId);

    @Suspendable
    void waitFor(int targetThreadId);

    @Suspendable
    void park();

    @Suspendable
    void unpark(int targetThreadId);

    @Suspendable
    void parkAndUnpark(int targetThreadId);

    @Suspendable
    void yield();

    @Suspendable
    int getCurrentThreadId();

    @Suspendable
    String getCurrentThreadName();
}
