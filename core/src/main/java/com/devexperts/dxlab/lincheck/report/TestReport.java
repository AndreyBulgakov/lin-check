package com.devexperts.dxlab.lincheck.report;

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
        this.result = builder.result == null ? Result.ERROR : builder.result;
    }

    //region Getters
    public String getTestName() {
        return testName;
    }

    public String getStrategyName() {
        return strategyName;
    }

    public int getMaxIterations() {
        return maxIterations;
    }

    public int getMaxInvocations() {
        return maxInvocations;
    }

    public String getThreadConfig() {
        return threadConfig;
    }

    public int getIterations() {
        return iterations;
    }

    public int getInvocations() {
        return invocations;
    }

    public long getTime() {
        return time;
    }

    public Result getResult() {
        return result;
    }
    //endregion

    @Override
    public String toString() {
        return "Testname = " + testName +
                ", StrategyName = " + strategyName +
                ", MaxIterations = " + maxIterations +
                ", MaxInvocations = " + maxInvocations +
                ", ThreadConfiguration = " + threadConfig +
                ", Iterations = " + iterations +
                ", Invocations = " + invocations +
                ", PassedTime = " + time +
                ", CurrentResult = " + result;
    }

    /**
     * Enum which specifies test result
     */
    public enum Result {
        SUCCESS, FAILURE, ERROR
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
         * Set name of the current test
         * @param testName simple className of test
         */
        public Builder name(String testName) {
            this.testName = testName;
            return this;
        }

        /**
         * Set strategy name
         */
        public Builder strategy(String strategyName) {
            this.strategyName = strategyName;
            return this;
        }

        /**
         * Set execution time of the test
         */
        public Builder time(long time) {
            this.time = time;
            return this;
        }

        /**
         * Set result of the test
         */
        public Builder result(Result result) {
            this.result = result;
            return this;
        }

        /**
         * Increment number current iteration
         */
        public Builder incIterations() {
            this.iterations++;
            return this;
        }

        /**
         * Increment number of current invocation
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

        public Builder setIterations(int iteration){
            this.iterations = iteration;
            return this;
        }

        public Builder setInvocations(int invocation){
            this.invocations = invocation;
            return this;
        }
    }
}
