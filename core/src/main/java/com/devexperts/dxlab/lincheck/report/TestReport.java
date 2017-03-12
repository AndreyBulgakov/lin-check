package com.devexperts.dxlab.lincheck.report;

import java.util.Objects;

public class TestReport {
    private final String testName;
    private final int maxIterations;
    private final int maxInvocations;
    private final String threadConfig;
    private final int iterations;
    private final int invocations;
    private final long time;
    private final Result result;

    private TestReport(Builder builder) {
        this.testName = Objects.requireNonNull(builder.testName);
        // TODO initialize other fields
    }

    @Override
    public String toString() {
        return "TestReport{" +
            "testName='" + testName + '\'' +
            ", maxIterations=" + maxIterations +
            ", maxInvocations=" + maxInvocations +
            ", threadConfig='" + threadConfig + '\'' +
            ", iterations=" + iterations +
            ", invocations=" + invocations +
            ", time=" + time +
            ", result=" + result +
            '}';
    }

    public enum Result {
        SUCCESS, FAILURE
    }

    public static class Builder {
        private String testName;
        private int maxIterations;
        private int maxInvocations;
        private String threadConfig;
        private int iterations;
        private int invocations;
        private long time;
        private Result result;

        public Builder withName(String testName) {
            this.testName = testName;
            return this;
        }

        public Builder maxIterations(int maxIterations) {
            this.maxIterations = maxIterations;
            return this;
        }

        public Builder incIterations() {
            this.iterations++;
        }

        // TODO add other methods

        public TestReport build() {
            return new TestReport(this);
        }
    }
}
