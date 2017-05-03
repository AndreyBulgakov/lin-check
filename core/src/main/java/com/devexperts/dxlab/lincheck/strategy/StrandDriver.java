package com.devexperts.dxlab.lincheck.strategy;

/*
 * #%L
 * core
 * %%
 * Copyright (C) 2015 - 2017 Devexperts, LLC
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

import co.paralleluniverse.fibers.SuspendExecution;
import co.paralleluniverse.fibers.Suspendable;
import co.paralleluniverse.strands.Strand;
import com.devexperts.dxlab.lincheck.ExecutionsStrandPool;

import java.util.concurrent.atomic.AtomicInteger;

public class StrandDriver implements Driver {

    public final ExecutionsStrandPool pool;

    public StrandDriver(ExecutionsStrandPool pool) {
        this.pool = pool;
    }

    @Suspendable
    @Override
    public void switchThread(AtomicInteger targetThreadId) {
        try {
            Strand srt = pool.getStrand(targetThreadId.get());
            Strand.unpark(srt);
            while (getCurrentThreadId() != targetThreadId.get()){
                Strand.park();
            }
        } catch (SuspendExecution suspendExecution) {
            suspendExecution.printStackTrace();
        }
    }

    @Suspendable
    @Override
    public void switchOnEndOfThread(AtomicInteger targetThreadId) {
        Strand.unpark(pool.getStrand(targetThreadId.get()));
    }

    @Suspendable
    @Override
    public void waitFor(AtomicInteger targetThreadId) {
        if (getCurrentThreadId() != targetThreadId.get()) {
            try {
                while (getCurrentThreadId() != targetThreadId.get()){
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