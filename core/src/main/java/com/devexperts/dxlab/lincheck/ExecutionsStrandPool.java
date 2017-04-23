package com.devexperts.dxlab.lincheck;

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

import co.paralleluniverse.fibers.Fiber;
import co.paralleluniverse.fibers.FiberExecutorScheduler;
import co.paralleluniverse.strands.Strand;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

/**
 * Executor that can initialize {@link Thread} or {@link co.paralleluniverse.fibers.Fiber} pool
 */
public class ExecutionsStrandPool {
    private final ArrayList<Strand> pool = new ArrayList<>();
    private final List<Future<Result[]>> futureTasks = new ArrayList<>();
    private final StrandType strandType;
    private final CallableStrandFactory FACTORY;
    private boolean isRuning = false;

    public enum StrandType {
        THREAD,
        FIBER
    }

    public StrandType getStrandType() {
        return strandType;
    }

    /**
     * Create new instance of ExecutionsStrandPool that scheduling Threads or Fibers
     *
     * @param type type of Strand can be Fiber or Thread
     */
    ExecutionsStrandPool(final StrandType type) {
        this.strandType = type;
        FiberExecutorScheduler exe = new FiberExecutorScheduler("demo", Runnable::run);
        if (type == StrandType.FIBER)
            this.FACTORY = callable -> {
                Fiber<Result[]> strand = new Fiber<>(exe, callable::call);
                String name = "LinCheckStrand";
                strand.setName(name);
                futureTasks.add(strand);
                pool.add(strand);
                return strand;
            };
        else
            this.FACTORY = callable -> {
                FutureTask<Result[]> futureTask = new FutureTask<>(callable);
                Strand strand = Strand.of(new Thread(futureTask));
                String name = "LinCheckStrand";
                strand.setName(name);
                futureTasks.add(futureTask);
                pool.add(strand);
                return strand;
            };
    }

    public ExecutionsStrandPool add(Collection<? extends TestThreadExecution> tasks) {
        if (isRuning) throw new IllegalStateException("Pool has already been running");
        tasks.forEach(FACTORY::newStrand);
        return this;
    }

    public ExecutionsStrandPool add(TestThreadExecution task) {
        if (isRuning) throw new IllegalStateException("Pool has already been running");
        FACTORY.newStrand(task);
        return this;
    }

    public int getStrandId(Strand strand) {
        return pool.indexOf(strand);
    }

    public Strand getStrand(int id) {
        return pool.get(id);
    }

    public Strand getNotCurrentStrandAndAlive() {
        Strand current = Strand.currentStrand();
        for (Strand strand : pool) {
            if (current != strand)
                return strand;
        }
        return null;
    }

    public List<Future<Result[]>> getFutures() {
        return futureTasks;
    }

    public void invokeOne(int id) {
        Strand strand = pool.get(id);
        if (!strand.isAlive() && !strand.isTerminated()) {
            strand.start();
            isRuning = true;
        }
    }

    public List<Future<Result[]>> invokeAll() {
        if (isRuning) throw new IllegalStateException("Pool has already been running");
        isRuning = true;
        for (Strand strand : pool) {
                strand.start();
//            if (!strand.isAlive())
        }
        return futureTasks;
    }

    public void clear() {
        pool.clear();
        futureTasks.clear();
        isRuning = false;
    }

    public boolean isTerminated() {
        return pool.stream().allMatch(Strand::isTerminated);
    }


    public boolean isInterrupted() {
        return pool.stream().allMatch(Strand::isInterrupted);
    }


    public boolean isAlive() {
        return pool.stream().allMatch(Strand::isAlive);
    }

    public boolean isDone() {
        return pool.stream().allMatch(Strand::isDone);
    }

    private interface CallableStrandFactory {
        Strand newStrand(TestThreadExecution callable);
    }

}
