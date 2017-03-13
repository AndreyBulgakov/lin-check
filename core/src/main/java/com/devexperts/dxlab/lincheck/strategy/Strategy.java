package com.devexperts.dxlab.lincheck.strategy;


/**
 * Interface that contains methods to insert them into program points
 * // TODO in which program points? Which strategies?
 */
public interface Strategy {

    /**
     * // TODO add onSharedVariable[Read,Write] methods and add javadoc to them
     * Execute implemented strategy
     * @param location location id
     */
    void onSharedVariableAccess(int location);
}
