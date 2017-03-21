package com.devexperts.dxlab.lincheck;

/**
 * Created by alexander on 21.03.17.
 */
public class LinCheckThread extends Thread {
    private final int id;
    private final TestThreadExecution execution;
    private Result[] results;

    public LinCheckThread(int id, TestThreadExecution execution) {
        this.id = id;
        this.execution = execution;
        this.start();
    }

    public int getThreadId() {
        return id;
    }

    public Result[] getResults() { return results; }

    @Override
    public void run() {
        results = execution.call();
    }
}
