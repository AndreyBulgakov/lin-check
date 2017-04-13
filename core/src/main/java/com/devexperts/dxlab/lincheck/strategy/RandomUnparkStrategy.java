package com.devexperts.dxlab.lincheck.strategy;

import co.paralleluniverse.fibers.Fiber;
import co.paralleluniverse.fibers.SuspendExecution;
import co.paralleluniverse.fibers.Suspendable;
import co.paralleluniverse.strands.Strand;
import com.devexperts.dxlab.lincheck.ExecutionsStrandPool;

import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.nio.file.StandardOpenOption.APPEND;


public class RandomUnparkStrategy implements Strategy {
    public static int out;
    volatile PrintStream logger = getLogger();
    ExecutionsStrandPool pool;

    public RandomUnparkStrategy(ExecutionsStrandPool pool) {
        this.pool = pool;
    }

    @Suspendable
    @Override
    public void onSharedVariableRead(int location) {
        try {
            if (Fiber.isCurrentFiber()) {
                Strand randomFiber = pool.getNotCurrentStrandAndAlive();
                logger.println("\t\tEnter on SharedRead");
                logger.println("\t\tCurrentLocation id" + location);
                logger.println("\t\tCurrent fiber state " + Strand.currentStrand().getState());
                logger.println("\t\tTarget fiber state " + randomFiber.getState());
                logger.println("\t\tTry to switch thread "
                        + pool.getStrandId(Strand.currentStrand()) + " to " + pool.getStrandId(randomFiber));
                logger.println();
                logger.flush();
                if (randomFiber.isAlive()) {
//                        Strand.un
                    if (randomFiber.getState() == Strand.State.RUNNING || randomFiber.getState() == Strand.State.STARTED) {
//                        Strand.unpark(Strand.currentStrand());
                        Strand.park();
                    } else {
                        Strand.parkAndUnpark(randomFiber);
                    }
                }
                out = 0;
            }
        } catch (SuspendExecution suspendExecution) {
            throw new AssertionError(suspendExecution);
        }
    }

    @Suspendable
    @Override
    public void onSharedVariableWrite(int location) {
        try {
            if (Fiber.isCurrentFiber()) {
                Strand randomFiber = pool.getNotCurrentStrandAndAlive();
                logger.println("\t\tEnter on SharedWrite");
                logger.println("\t\tCurrentLocation id" + location);
                logger.println("\t\tCurrent fiber state " + Strand.currentStrand().getState());
                logger.println("\t\tTarget fiber state " + randomFiber.getState());
                logger.println("\t\tTry to switch thread "
                        + pool.getStrandId(Strand.currentStrand()) + " to " + pool.getStrandId(randomFiber));
                logger.println();
                logger.flush();
                if (randomFiber.isAlive()) {
                    if (randomFiber.getState() == Strand.State.RUNNING || randomFiber.getState() == Strand.State.STARTED) {
//                        Strand.unpark(Strand.currentStrand());
                        Strand.park();
                    } else {
                        Strand.parkAndUnpark(randomFiber);
                    }
                }
                out = 0;
            }

        } catch (SuspendExecution suspendExecution) {
            throw new AssertionError(suspendExecution);
        }
    }

    @Suspendable
    @Override
    public void endOfThread() {
        if (Fiber.isCurrentFiber()) {
            Strand randomFiber = pool.getNotCurrentStrandAndAlive();
            logger.println("\t\tEnter on End of thread " + pool.getStrandId(Strand.currentStrand()));
            logger.println("\t\tTry to unpark" + pool.getStrandId(randomFiber));
            logger.println();
            logger.flush();
            Fiber.unpark(randomFiber);
        }
        out = 0;
    }

    //    public Fiber getRandomParkedFiber() {
//        int currentId = pool.getStrandId(Strand.currentStrand());
//        Fiber current = Fiber.currentFiber();
//        for (Fiber fiber : StrategyHolder.fibers) {
//            if (fiber != current) {
//                return fiber;
//            }
//        }
//        return current;
//    }
    public static PrintStream getLogger() {
        try {
            Path p = Paths.get(System.getProperty("user.dir"), "log");
            if (Files.exists(p)) {
//                return System.out;
                return new PrintStream(Files.newOutputStream(p, APPEND));
            } else {
//                return System.out;
                return new PrintStream(Files.newOutputStream(p));
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }
}
