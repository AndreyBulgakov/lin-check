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


    /**
     * Execute implemented strategy at end of thread
     */
    default void endOfThread() {
    }

    /**
     * Execute implemented strategy at start of thread
     */
    default void startOfThread() {
    }

    default void beforeStartIteration(int threadNumber) {
    }

    default void onStartIteration() {
    }

    default void onStartInvocation(int iteration, int invocation) {
    }

    default void onEndInvocation() {
    }

    default void onEndIteration() {
    }

    default boolean isNeedStopIteration() {
        return false;
    }

}
