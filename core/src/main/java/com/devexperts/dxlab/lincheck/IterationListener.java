package com.devexperts.dxlab.lincheck;


import com.devexperts.dxlab.lincheck.report.TestReport;
import com.devexperts.dxlab.lincheck.strategy.Strategy;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class IterationListener {

    private Queue<TestReport> reports = new ConcurrentLinkedQueue<TestReport>();
    private final long START_TIME;
    private final Object LOCK;
    private final CTestConfiguration cfg;
    private final String testClassName;
    private final int maxIterations;

    private boolean isNonLinearizable = false;
    private volatile int currentIteration = 0;

    public IterationListener(Object lock, CTestConfiguration cfg, String testClassName, long START_TIME) {
        this.START_TIME = START_TIME;
        this.LOCK = lock;
        this.cfg = cfg;
        this.testClassName = testClassName;
        this.maxIterations = cfg.getIterations();
    }

    public void foundNonLinearizable(Strategy strategy, int iteration, int invocation){
        registerReport(strategy,iteration,invocation);
        isNonLinearizable = true;
        synchronized (LOCK){
            LOCK.notify();
        }
    }
    public void registerReport(Strategy strategy, int iteration, int invocation){
        TestReport report = new TestReport.Builder(cfg)
                .name(testClassName)
                .strategy(strategy.getClass().getSimpleName())
                .setInvocations(invocation)
                .setIterations(iteration)
                .time(Instant.now().toEpochMilli() - START_TIME)
                .result(TestReport.Result.FAILURE)
                .build();
        reports.add(report);
    }

    public void onEndIteration(){
        currentIteration++;
        System.out.println(currentIteration);
        if (currentIteration >= maxIterations){
            synchronized (LOCK){
                LOCK.notify();
            }
        }
    }

    public boolean isNonLinearizable() {
        return isNonLinearizable;
    }
}
