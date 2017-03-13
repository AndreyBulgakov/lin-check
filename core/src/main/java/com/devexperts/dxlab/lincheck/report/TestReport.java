package com.devexperts.dxlab.lincheck.report;

import com.devexperts.dxlab.lincheck.CTestConfiguration;

import java.util.List;
import java.util.Objects;

public class TestReport {
    private final String testName;
    private final String strategyName;
    private final int maxIterations;
    private final int maxInvocations;
    private final String threadConfig;
    private final int iterations;
    private final int invocations;
    private final long time;
    private final Result result;

    private TestReport(Builder builder) {
        this.testName = Objects.requireNonNull(builder.testName);
        this.strategyName = Objects.requireNonNull(builder.strategyName);
        this.maxIterations = Objects.requireNonNull(builder.maxIterations);
        this.maxInvocations = Objects.requireNonNull(builder.maxInvocations);
        this.threadConfig = Objects.requireNonNull(builder.threadConfig);
        this.invocations = Objects.requireNonNull(builder.invocations);
        this.iterations = Objects.requireNonNull(builder.iterations);
        this.time = Objects.requireNonNull(builder.time);
        this.result = Objects.requireNonNull(builder.result);
    }

    @Override
    public String toString() {
        return testName + ", " + strategyName + ", " + maxIterations + ", " + maxInvocations + ", " + threadConfig +
                ", " + iterations + ", " + invocations + ", " + time + ", " + result;
    }

    public enum Result {
        SUCCESS, FAILURE
    }

    public static class Builder {
        private String testName;
        private String strategyName;
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

        public Builder withStrategy(String strategyName) {
            this.strategyName = strategyName;
            return this;
        }

        public Builder maxIterations(int maxIterations) {
            this.maxIterations = maxIterations;
            return this;
        }

        public Builder maxInvocations(int maxInvocations) {
            this.maxInvocations = maxInvocations;
            return this;
        }

        public Builder threadConfig(List<CTestConfiguration.TestThreadConfiguration> threadConfigurations) {
            this.threadConfig = threadConfigurations.toString();
            return this;
        }

        public Builder time(long time) {
            this.time = time;
            return this;
        }

        public Builder result(Result result) {
            this.result = result;
            return this;
        }

        public Builder incIterations() {
            this.iterations++;
            return this;
        }

        public Builder incInvocations() {
            this.invocations++;
            return this;
        }

        public TestReport build() {
            return new TestReport(this);
        }
    }
}
