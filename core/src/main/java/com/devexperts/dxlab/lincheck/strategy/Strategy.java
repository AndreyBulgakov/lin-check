package com.devexperts.dxlab.lincheck.strategy;


/**
 * Interface to implement strategy to checking interleaving points
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
