package com.devexperts.dxlab.lincheck.strategy;

import co.paralleluniverse.fibers.SuspendExecution;
import co.paralleluniverse.fibers.Suspendable;
import co.paralleluniverse.strands.Strand;
import com.devexperts.dxlab.lincheck.ExecutionsStrandPool;

import java.util.concurrent.atomic.AtomicInteger;

//TODO delete +1 -1 in targetThreadId
public class StrandDriver implements Driver {

    public final ExecutionsStrandPool pool;

    public StrandDriver(ExecutionsStrandPool pool) {
        this.pool = pool;
    }

    @Suspendable
    @Override
    public void switchThread(AtomicInteger targetThreadId) {
        try {
            Strand srt = pool.getStrand(targetThreadId.get() - 1);
            Strand.unpark(srt);
            while ((getCurrentThreadId() + 1) != targetThreadId.get()){
                Strand.park();
            }
        } catch (SuspendExecution suspendExecution) {
            suspendExecution.printStackTrace();
        }
    }

    @Suspendable
    @Override
    public void switchOnEndOfThread(AtomicInteger targetThreadId) {
        Strand.unpark(pool.getStrand(targetThreadId.get() - 1));
    }

    @Suspendable
    @Override
    public void waitFor(AtomicInteger targetThreadId) {
        if (getCurrentThreadId() + 1 != targetThreadId.get()) {
            try {
                while ((getCurrentThreadId() + 1) != targetThreadId.get()){
                    Strand.park();
                }
            } catch (SuspendExecution suspendExecution) {
                suspendExecution.printStackTrace();
            }
        }
    }

    @Suspendable
    @Override
    public void block() {
        try {
            Strand.park();
        } catch (SuspendExecution suspendExecution) {
            throw new IllegalStateException("Driver cant block thread", suspendExecution);
        }
    }

    @Override
    public void unblock(int targetThreadId) {
        Strand.unpark(pool.getStrand(targetThreadId));
    }

    @Suspendable
    @Override
    public void blockAndUnblock(int targetThreadId) {
        try {
            Strand.parkAndUnpark(pool.getStrand(targetThreadId));
        } catch (SuspendExecution suspendExecution) {
            throw new IllegalStateException("Driver can't block or unblock thread", suspendExecution);
        }

    }

    @Suspendable
    @Override
    public void yield() {
        try {
            Strand.yield();
        } catch (SuspendExecution suspendExecution) {
            throw new IllegalStateException("Driver cant yield thread", suspendExecution);
        }
    }

    @Override
    public int getCurrentThreadId() {
        return pool.getStrandId(Strand.currentStrand());
    }

    @Override
    public String getCurrentThreadName() {
        return Strand.currentStrand().getName();
    }
}
