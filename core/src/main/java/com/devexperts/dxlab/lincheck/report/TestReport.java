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

    /**
     * Enum for specify result of test
     */
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

        /**
         * Set name of current test
         * @param testName simple className of test
         * @return
         */
        public Builder withName(String testName) {
            this.testName = testName;
            return this;
        }

        /**
         * Set name of current strategy
         * @param strategyName simple className of strategy
         * @return
         */
        public Builder withStrategy(String strategyName) {
            this.strategyName = strategyName;
            return this;
        }

        /**
         * Set maximum of iterations for test configuration
         * @param maxIterations
         * @return
         */
        public Builder maxIterations(int maxIterations) {
            this.maxIterations = maxIterations;
            return this;
        }

        /**
         * Set maximum of invocations for test configuration
         * @param maxInvocations
         * @return
         */
        public Builder maxInvocations(int maxInvocations) {
            this.maxInvocations = maxInvocations;
            return this;
        }

        /**
         * Set thread configurations for test configuration
         * @param threadConfigurations
         * @return
         */
        public Builder threadConfig(List<CTestConfiguration.TestThreadConfiguration> threadConfigurations) {
            this.threadConfig = threadConfigurations.toString();
            return this;
        }

        /**
         * Set time from start to end of test
         * @param time
         * @return
         */
        public Builder time(long time) {
            this.time = time;
            return this;
        }

        /**
         * Set result of test
         * @param result
         * @return
         */
        public Builder result(Result result) {
            this.result = result;
            return this;
        }

        /**
         * Increment current iteration
         * @return
         */
        public Builder incIterations() {
            this.iterations++;
            return this;
        }

        /**
         * Increment current invocation
         * @return
         */
        public Builder incInvocations() {
            this.invocations++;
            return this;
        }

        /**
         * Generate TestReport
         * @return
         */
        public TestReport build() {
            return new TestReport(this);
        }
    }
}
