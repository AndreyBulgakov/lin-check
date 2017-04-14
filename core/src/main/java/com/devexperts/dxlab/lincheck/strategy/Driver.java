package com.devexperts.dxlab.lincheck.strategy;


public interface Driver {
    void switchThread(int targetThreadId);

    void switchOnEndOfThread(int targetThreadId);

    void waitFor(int targetThreadId);

    void park();

    void unpark(int targetThreadId);

    void parkAndUnpark(int targetThreadId);

    void yield();

    int getCurrentThreadId();

    String getCurrentThreadName();
}
