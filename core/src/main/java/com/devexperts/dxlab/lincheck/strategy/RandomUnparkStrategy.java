package com.devexperts.dxlab.lincheck.strategy;

import co.paralleluniverse.fibers.Fiber;
import co.paralleluniverse.fibers.SuspendExecution;
import co.paralleluniverse.fibers.Suspendable;


public class RandomUnparkStrategy implements Strategy {
    public static int out;

    @Suspendable
    @Override
    public void onSharedVariableRead(int location) {
        try {
            if (Fiber.isCurrentFiber()) {
                Fiber.parkNanos(100);
//                Fiber randomFiber = getRandomParkedFiber();
//                Fiber.parkAndUnpark(randomFiber);
                out = 0;
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
                Fiber.parkNanos(100);
//                Fiber randomFiber = getRandomParkedFiber();
//                Fiber.parkAndUnpark(randomFiber);
                out = 0;
//                out++;
            }

        } catch (SuspendExecution suspendExecution) {
            throw new AssertionError(suspendExecution);
        }
//        Thread.yield();
    }

    @Suspendable
    @Override
    public void endOfThread() {
        if (Fiber.isCurrentFiber())
            Fiber.unpark(getRandomParkedFiber());
    }

    public Fiber getRandomParkedFiber() {
        Fiber current = Fiber.currentFiber();
        for (Fiber fiber : StrategyHolder.fibers) {
            if (fiber != current && !fiber.isTerminated()) {
                return fiber;
            }
        }
        return current;
    }
}
