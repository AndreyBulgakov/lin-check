package com.devexperts.dxlab.lincheck.report;

import com.devexperts.dxlab.lincheck.CTestConfiguration;

import java.util.Objects;

/**
 * Class with CTest report parameters
 * Use {@link Builder} for creating
 * <ul>
 *     <li>testName - name of CTest</li>
 *     <li>strategyName - name of strategy of checking interleaving points</li>
 *     <li>maxIteration - specified in CTest running iterations</li>
 *     <li>maxInvocation - specified in CTest running invocations</li>
 *     <li>time - time from start to end of testing in milliSeconds</li>
 *     <li>result - result of running test</li>
 * </ul>
 */
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
        this.maxIterations = builder.maxIterations;
        this.maxInvocations = builder.maxInvocations;
        this.threadConfig = Objects.requireNonNull(builder.threadConfig);
        this.invocations = builder.invocations;
        this.iterations = builder.iterations;
        this.time = builder.time;
        this.result = Objects.requireNonNull(builder.result);
    }

    @Override
    public String toString() {
        return testName + ", " + strategyName + ", " + maxIterations + ", " + maxInvocations + ", " + threadConfig +
                ", " + iterations + ", " + invocations + ", " + time + ", " + result;
    }

    /**
     * Enum which specifies test result
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

        public Builder(CTestConfiguration testConfiguration) {
            this.maxIterations = testConfiguration.getIterations();
            this.maxInvocations = testConfiguration.getInvocationsPerIteration();
            this.threadConfig = testConfiguration.getThreadConfigurations().toString();
        }

        /**
         * Set name of current test
         * @param testName simple className of test
         */
        public Builder name(String testName) {
            this.testName = testName;
            return this;
        }

        /**
         * Set name of current strategy
         * @param strategyName simple className of strategy
         */
        public Builder strategy(String strategyName) {
            this.strategyName = strategyName;
            return this;
        }

        /**
         * Set time from start to end of test
         * @param time
         */
        public Builder time(long time) {
            this.time = time;
            return this;
        }

        /**
         * Set result of test
         * @param result result of running
         */
        public Builder result(Result result) {
            this.result = result;
            return this;
        }

        /**
         * Increment current iteration
         */
        public Builder incIterations() {
            this.iterations++;
            return this;
        }

        /**
         * Increment current invocation
         */
        public Builder incInvocations() {
            this.invocations++;
            return this;
        }

        /**
         * Generate TestReport
         */
        public TestReport build() {
            return new TestReport(this);
        }
    }
}
