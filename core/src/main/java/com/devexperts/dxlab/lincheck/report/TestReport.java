package com.devexperts.dxlab.lincheck.report;

public class TestReport {
    private String testName;
    private String strategyName;
    private int maxIteraions;
    private int maxInvocations;
    private String threadConfig;
    private int wasIterations;
    private int wasInvokations;
    private long passedTime;
    private String result;

    public TestReport(String testName, String strategyName){
        this.testName = testName;
        this.strategyName = strategyName;
    }

    public void addInvokations(int inv) {
        wasInvokations += inv;
    }

    public void setWasIterations(int iterations) {
        wasIterations = iterations;
    }

    public void setPassedTime(long time) {
        passedTime = time;
    }

    public void setConfiguration(int maxIteraions, int maxInvocations, String threadConfig) {
        this.maxIteraions = maxIteraions;
        this.maxInvocations = maxInvocations;
        this.threadConfig = threadConfig;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public int getMaxIteraions() {
        return maxIteraions;
    }

    @Override
    public String toString() {
        return testName + "," + strategyName + "," + maxIteraions + "," + maxInvocations +
                "," + threadConfig + "," + wasIterations + "," + wasInvokations + "," + passedTime + "," + result + "\n";
    }
}
