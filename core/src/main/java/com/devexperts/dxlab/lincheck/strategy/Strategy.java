package com.devexperts.dxlab.lincheck.strategy;


/**
 * Each strategy should implement this interface and handle shared variable reading and writing.
 */
public interface Strategy {

    /**
     * Execute implemented strategy on read operations
     * @param location location id
     */
    void onSharedVariableRead(int location);

    /**
     * Execute implemented strategy on write operations
     *
     * @param location location id
     */
    void onSharedVariableWrite(int location);
}
