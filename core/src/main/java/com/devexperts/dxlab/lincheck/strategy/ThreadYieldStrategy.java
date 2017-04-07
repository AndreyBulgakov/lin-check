package com.devexperts.dxlab.lincheck.strategy;

import co.paralleluniverse.fibers.Fiber;
import co.paralleluniverse.fibers.SuspendExecution;
import co.paralleluniverse.fibers.Suspendable;

/**
 * This strategy invokes {@link Thread#yield()} method on every shared variable access.
 */
public class ThreadYieldStrategy implements Strategy {
    @Suspendable
    @Override
    public void onSharedVariableRead(int location) {

        try {

            if (Fiber.isCurrentFiber()) {
                Fiber.yield();
                System.out.println("after park...");
            }

        } catch (SuspendExecution suspendExecution) {
            throw new AssertionError(suspendExecution);
        }
//        Thread.yield();
    }

    @Suspendable
    @Override
    public void onSharedVariableWrite(int location) {
        try {
            if (Fiber.isCurrentFiber()) {
                Fiber.yield();
                System.out.println("after park...");
            }
        } catch (SuspendExecution suspendExecution) {
            throw new AssertionError(suspendExecution);
        }
//        Thread.yield();
    }
}
