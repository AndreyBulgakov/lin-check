package com.devexperts.dxlab.lincheck.report;

public class TestReport {
    private String testName;
    private String strategyName;
    private int maxIterations;
    private int maxInvocations;
    private String threadConfig;
    private int wasIterations;
    private int wasInvocations;
    private long passedTime;
    private String result;

    public TestReport(String testName, String strategyName){
        this.testName = testName;
        this.strategyName = strategyName;
    }

    public void addInvokations(int inv) {
        wasInvocations += inv;
    }

    public void setWasIterations(int iterations) {
        wasIterations = iterations;
    }

    public void setPassedTime(long time) {
        passedTime = time;
    }

    public void setConfiguration(int maxIteraions, int maxInvocations, String threadConfig) {
        this.maxIterations = maxIteraions;
        this.maxInvocations = maxInvocations;
        this.threadConfig = threadConfig;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public int getMaxIterations() {
        return maxIterations;
    }

    @Override
    public String toString() {
        return testName + "," + strategyName + "," + maxIterations + "," + maxInvocations +
                "," + threadConfig + "," + wasIterations + "," + wasInvocations + "," + passedTime + "," + result + "\n";
    }
}
