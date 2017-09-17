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


import com.devexperts.dxlab.lincheck.report.TestReport;
import com.devexperts.dxlab.lincheck.strategy.Strategy;

import java.time.Instant;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

class IterationListener {

    private Queue<TestReport> reports = new ConcurrentLinkedQueue<TestReport>();
    private final long START_TIME;
    private final Object LOCK;
    private final CTestConfiguration cfg;
    private final String testClassName;
    private final int maxIterations;

    private boolean isNonLinearizable = false;

    IterationListener(Object lock, CTestConfiguration cfg, String testClassName, long START_TIME) {
        this.START_TIME = START_TIME;
        this.LOCK = lock;
        this.cfg = cfg;
        this.testClassName = testClassName;
        this.maxIterations = cfg.getIterations();
    }

    void foundNonLinearizable(Strategy strategy, int iteration, int invocation){
        registerReport(strategy,iteration,invocation);
        isNonLinearizable = true;
        throw new AssertionError("Non-linearizable");
    }
    private void registerReport(Strategy strategy, int iteration, int invocation){
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


    private AtomicInteger currentIteration = new AtomicInteger(0);
    void onEndIteration(){
        int i = currentIteration.incrementAndGet();
        System.out.println(i + "/" + maxIterations);
        if (i >= maxIterations){
            synchronized (LOCK){
                LOCK.notify();
            }
        }
    }

    boolean isNonLinearizable() {
        return isNonLinearizable;
    }
}
