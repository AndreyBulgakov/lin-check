package com.devexperts.dxlab.lincheck;

import co.paralleluniverse.fibers.Fiber;
import co.paralleluniverse.strands.Strand;
import co.paralleluniverse.strands.SuspendableUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

/**
 * Executor that can initialize {@link Thread} or {@link co.paralleluniverse.fibers.Fiber} pool
 */
public class StrandPoolBuilder<T> {
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
     * Create new instance of StrandPoolBuilder that scheduling Threads or Fibers
     *
     * @param type type of Strand tha
     */
    private StrandPoolBuilder(final StrandType type) {
        this.strandType = type;
        if (type == StrandType.FIBER)
            this.FACTORY = callable -> {
                Fiber<T> fiber = new Fiber<>(SuspendableUtils.asSuspendable(callable));
                futureTasks.add(fiber);
                pool.add(fiber);
                return fiber;
            };
        else
            this.FACTORY = callable -> {
                FutureTask<T> futureTask = new FutureTask<>(callable);
                Strand strand = Strand.of(new Thread(futureTask));
                futureTasks.add(futureTask);
                pool.add(strand);
                return strand;
            };

    }

    public StrandPoolBuilder add(Collection<? extends Callable<T>> tasks) {
//        switch (strandType){
//            case FIBER: tasks.forEach(this::newFiberStrand);
//            case THREAD: tasks.forEach(this::newThreadStrand);
//        }
        if (isRuning) throw new IllegalStateException("Pool has already been running");
        tasks.forEach(FACTORY::newStrand);
        return this;
    }

    public StrandPoolBuilder add(Callable<T> task) {
        if (isRuning) throw new IllegalStateException("Pool has already been running");
        FACTORY.newStrand(task);
        return this;
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


//    private Strand newFiberStrand(Callable<T> target){
//        Fiber<T> fiber = new Fiber<>(SuspendableUtils.asSuspendable(target));
//        futureTasks.add(fiber);
//        pool.add(fiber);
//        return fiber;
//    }
//    private Strand newThreadStrand(Callable<T> target){
//        FutureTask<T> futureTask = new FutureTask<>(target);
//        Strand strand = Strand.of(new Thread(futureTask));
//        futureTasks.add(futureTask);
//        pool.add(strand);
//        return strand;
//    }

    private interface CallableStrandFactory<C> {
        Strand newStrand(Callable<C> callable);
    }
}
