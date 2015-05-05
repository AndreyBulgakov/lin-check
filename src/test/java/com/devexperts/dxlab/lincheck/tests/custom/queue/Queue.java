package com.devexperts.dxlab.lincheck.tests.custom.queue;

public interface Queue {
    public void put(int x) throws QueueFullException;
    public int get() throws QueueEmptyException;
}
