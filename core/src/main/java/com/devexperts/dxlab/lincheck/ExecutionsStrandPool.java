package com.devexperts.dxlab.lincheck;

import co.paralleluniverse.fibers.Fiber;
import co.paralleluniverse.fibers.instrument.SuspendableHelper;
import co.paralleluniverse.strands.Strand;
import co.paralleluniverse.strands.SuspendableCallable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

/**
 * Executor that can initialize {@link Thread} or {@link co.paralleluniverse.fibers.Fiber} pool
 */
public class ExecutionsStrandPool<T> {
    //implements ExecutorService
    private final ArrayList<Strand> pool = new ArrayList<>();
    private final List<Future<T>> futureTasks = new ArrayList<>();
    private final StrandType strandType;
    private final CallableStrandFactory<T> FACTORY;
    private boolean isRuning = false;

    public static enum StrandType {
        THREAD,
        FIBER
    }

    public StrandType getStrandType() {
        return strandType;
    }

    /**
     * Create new instance of ExecutionsStrandPool that scheduling Threads or Fibers
     *
     * @param type type of Strand tha
     */
    ExecutionsStrandPool(final StrandType type) {
        System.out.println("===" + SuspendableHelper.isInstrumented(this.getClass()));
        this.strandType = type;
        if (type == StrandType.FIBER)
            this.FACTORY = callable -> {
                Fiber<T> fiber = new Fiber<T>(callable);
//                Fiber<T> fiber = new Fiber<>();
                futureTasks.add(fiber);
                pool.add(fiber);
                return fiber;
            };
        else
            this.FACTORY = callable -> {
                FutureTask<T> futureTask = new FutureTask<>(callable::run);
                Strand strand = Strand.of(new Thread(futureTask));
                futureTasks.add(futureTask);
                pool.add(strand);
                return strand;
            };
    }

    public ExecutionsStrandPool<T> add(Collection<? extends SuspendableCallable<T>> tasks) {
        if (isRuning) throw new IllegalStateException("Pool has already been running");
        tasks.forEach(FACTORY::newStrand);
        return this;
    }

    public ExecutionsStrandPool<T> add(SuspendableCallable<T> task) {
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

    public List<Future<T>> getFutures() {
        return futureTasks;
    }

    public void invokeOne(int id) {
        Strand strand = pool.get(id);
//        Future<T> future = futureTasks.get(id);
        if (!strand.isAlive() && !strand.isTerminated()) {
            strand.start();
//            return future;
        }
    }

    public List<Future<T>> invokePool() {
        if (isRuning) throw new IllegalStateException("Pool has already been running");
        isRuning = true;
        for (Strand strand : pool) {
            if (!strand.isAlive() && !strand.isTerminated())
                strand.start();
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

    private interface CallableStrandFactory<C> {
        Strand newStrand(SuspendableCallable<C> callable);
    }

}
