package com.devexperts.dxlab.lincheck.strategy;

/**
 * Interface that contains methods to insert them into program points
 */
public interface Strategy {

    /**
     * Execute implemented strategy
     * @param location id,
     * @see com.devexperts.dxlab.lincheck.transformers.ElementId as id implementation
     */
    void onSharedVariableAccess(int location);
}
