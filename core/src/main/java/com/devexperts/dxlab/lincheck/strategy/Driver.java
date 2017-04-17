package com.devexperts.dxlab.lincheck.strategy;


import co.paralleluniverse.fibers.Suspendable;

import java.util.concurrent.atomic.AtomicInteger;

public interface Driver {
    @Suspendable
    void switchThread(AtomicInteger targetThreadId);

    @Suspendable
    void switchOnEndOfThread(AtomicInteger targetThreadId);

    @Suspendable
    void waitFor(AtomicInteger targetThreadId);

    @Suspendable
    void block();

    @Suspendable
    void unblock(int targetThreadId);

    @Suspendable
    void blockAndUnblock(int targetThreadId);

    @Suspendable
    void yield();

    @Suspendable
    int getCurrentThreadId();

    @Suspendable
    String getCurrentThreadName();
}
