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

import co.paralleluniverse.fibers.Fiber;
import co.paralleluniverse.fibers.Suspendable;
import co.paralleluniverse.fibers.instrument.Retransform;
import co.paralleluniverse.fibers.instrument.SuspendableHelper;
import com.devexperts.dxlab.lincheck.report.Reporter;
import com.devexperts.dxlab.lincheck.report.TestReport;
import com.devexperts.dxlab.lincheck.strategy.EnumerationStrategy;
import com.devexperts.dxlab.lincheck.strategy.StrategyHolder;
import com.devexperts.dxlab.lincheck.strategy.ThreadYieldStrategy;
import javafx.util.Pair;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * TODO documentation
 * TODO logging
 * TODO avoid executions without write operations
 */
public class LinChecker0 {
    private static final boolean WRITE_REPORT = Boolean.parseBoolean(System.getProperty("lincheck.writeReport", "false"));
    private static final int MAX_WAIT = 1000;

    private final Random random = new Random(0);
    private final String testClassName;
    private final List<CTestConfiguration> testConfigurations;
    private final CTestStructure testStructure;

    LinChecker0(Class testClass) {
        this.testClassName = testClass.getCanonicalName();
        this.testConfigurations = CTestConfiguration.getFromTestClass(testClass);
        this.testStructure = CTestStructure.getFromTestClass(testClass);
        Retransform.addWaiver("com.devexperts.dxlab.lincheck.tests.counter.SimpleWrongCounter1", "incrementAndGet");
        Retransform.addWaiver(testClassName, "incAndGet");
//        Retransform.
    }

    // TODO do not pass instance, remove this method
    LinChecker0(Object testInstance) {
        this.testClassName = testInstance.getClass().getCanonicalName();
        Class<?> testClass = testInstance.getClass();
        this.testConfigurations = CTestConfiguration.getFromTestClass(testClass);
        this.testStructure = CTestStructure.getFromTestClass(testClass);
    }
//
//    /**
//     * LinChecker run method. Use LinChecker.check(TestClass.class) in junit test class
//     * @param testClass class that contains CTest
//     * @throws AssertionError if find Non-linearizable executions
//     */
//    public static void check(Class testClass) throws AssertionError {
//            new LinChecker0(testClass).check();
//    }

    /**
     * LinChecker run method. Use LinChecker.check(TestClass.class) in junit test class
     *
     * @param testInstance class that contains CTest
     * @throws AssertionError if find Non-linearizable executions
     */
    public static void check(Object testInstance) throws AssertionError {
        new LinChecker0(testInstance).check();
    }

    /**
     * @throws AssertionError if atomicity violation is detected
     */
    void check() throws AssertionError {
        testConfigurations.forEach((testConfiguration) -> {
            try {
//                checkImpl(testConfiguration);
                checkImplFiber(testConfiguration);
            } catch (InterruptedException | ClassNotFoundException | IllegalAccessException | InstantiationException e) {
                throw new IllegalStateException(e);
            }
        });
    }

    //region Generators
    private List<Actor> generateActorsForThread(CTestConfiguration.TestThreadConfiguration threadCfg) {
        int actorsInThread = threadCfg.minActors + random.nextInt(threadCfg.maxActors - threadCfg.minActors + 1);
        return random.ints(actorsInThread, 0, testStructure.getActorGenerators().size()) // random indexes
                .mapToObj(i -> testStructure.getActorGenerators().get(i)) // random actor generators
                .map(ActorGenerator::generate) // generate actors
                .collect(Collectors.toList()); // return as list
    }

    private List<List<Actor>> generateActors(CTestConfiguration testConfiguration) {
        return testConfiguration.getThreadConfigurations().stream()
                .map(this::generateActorsForThread)
                .collect(Collectors.toList());
    }

    private List<List<Actor>> generateAllLinearizableExecutions(List<List<Actor>> actorsPerThread) {
        List<List<Actor>> executions = new ArrayList<>();
        generateLinearizableExecutions0(executions, actorsPerThread, new ArrayList<>(), new int[actorsPerThread.size()],
                actorsPerThread.stream().mapToInt(List::size).sum());
        return executions;
    }

    private void generateLinearizableExecutions0(List<List<Actor>> executions, List<List<Actor>> actorsPerThread,
                                                 ArrayList<Actor> currentExecution, int[] indexes, int length) {
        if (currentExecution.size() == length) {
            executions.add((List<Actor>) currentExecution.clone());
            return;
        }
        for (int i = 0; i < indexes.length; i++) {
            List<Actor> actors = actorsPerThread.get(i);
            if (indexes[i] == actors.size())
                continue;
            currentExecution.add(actors.get(indexes[i]));
            indexes[i]++;
            generateLinearizableExecutions0(executions, actorsPerThread, currentExecution, indexes, length);
            indexes[i]--;
            currentExecution.remove(currentExecution.size() - 1);
        }
    }

    private Set<List<List<Result>>> generatePossibleResults(List<List<Actor>> actorsPerThread, Object testInstance,
                                                            ExecutionClassLoader loader) {
        return generateAllLinearizableExecutions(actorsPerThread).stream()
                .map(linEx -> { // For each permutation
                    List<Result> results = executeActors(linEx, testInstance, loader);
                    Map<Actor, Result> resultMap = new IdentityHashMap<>();
                    for (int i = 0; i < linEx.size(); i++) {
                        resultMap.put(linEx.get(i), results.get(i));
                    }
                    // Map result from single-execution permutation
                    // to each non-execution actorsPerThread List
                    return actorsPerThread.stream()
                            .map(actors -> actors.stream()
                                    .map(resultMap::get)
                                    .collect(Collectors.toList())
                            ).collect(Collectors.toList());
                }).collect(Collectors.toSet());
    }
    //endregion

    private void checkImpl(CTestConfiguration testCfg) throws InterruptedException, ClassNotFoundException, IllegalAccessException, InstantiationException {
        // Fixed thread pool executor to run TestThreadExecution
        //ExecutorService pool = Executors.newFixedThreadPool(testCfg.getThreads());
        // Store start time for counting performance metrics
        Instant startTime = Instant.now();
        // Create report builder
        TestReport.Builder reportBuilder = new TestReport.Builder(testCfg)
                .name(testClassName);
        try {
            // Reusable phaser
            final Phaser phaser = new Phaser(testCfg.getThreads());
            //Set strategy and initialize transformation in classes
            //Strategy currentStrategy = new ConsumeCPUStrategy(100);
            EnumerationStrategy currentStrategy = new EnumerationStrategy();
            StrategyHolder.setCurrentStrategy(currentStrategy);
            reportBuilder.strategy(currentStrategy.getClass().getSimpleName().replace("Strategy", ""));

            List<Integer> list = IntStream.range(1, testCfg.getThreads() + 1).boxed().collect(Collectors.toList());
            //список возможных запусков
            List<List<Integer>> enumer = EnumerationStrategy.threadPermutations(list);
            // Run iterations
            for (int iteration = 1; iteration <= 1; iteration++) {

                //индекс потока, в котором делаем прерывание
                int firstInteleavingThreadIndex = 0;
                int startScheduleIndex = 0;
                //первый возможный запуск
                List<Integer> threadQueue = enumer.get(startScheduleIndex);

                currentStrategy.setExecutionParameters(threadQueue, new Pair<>(threadQueue.get(firstInteleavingThreadIndex),
                        threadQueue.get(++firstInteleavingThreadIndex)));


                boolean needNextIteration = false;
                currentStrategy.prepareIteration();
                reportBuilder.incIterations();

                //Create loader, load and instantiate testInstance by this loader
                final ExecutionClassLoader loader = new ExecutionClassLoader(testClassName);
                final Object testInstance = loader.loadClass(testClassName).newInstance();

                List<List<Actor>> actorsPerThread = generateActors(testCfg);
                printIterationHeader(iteration, actorsPerThread);
                // Create TestThreadExecution's
                List<TestThreadExecution> testThreadExecutions = actorsPerThread.stream()
                        .map(actors -> TestThreadExecutionGenerator.create(testInstance, phaser, actors, false, loader))
                        .collect(Collectors.toList());

                Set<List<List<Result>>> possibleResultsSet = generatePossibleResults(actorsPerThread, testInstance, loader);

                // Run invocations
                for (int invocation = 1; invocation <= testCfg.getInvocationsPerIteration() && !needNextIteration; invocation++) {
                    currentStrategy.printHeader(iteration, invocation);
                    reportBuilder.incInvocations();
                    // Reset the state of test
                    invokeReset(testInstance);

                    // Run multithreaded test and get operation results for each thread
                    LinCheckThread[] threads = new LinCheckThread[testThreadExecutions.size()];
                    FutureTask<Result[]>[] resultsTask = new FutureTask[testThreadExecutions.size()];
                    for (int i = 0; i < testThreadExecutions.size(); i++) {
                        resultsTask[i] = new FutureTask<Result[]>(testThreadExecutions.get(i));
                        threads[i] = new LinCheckThread(i + 1, resultsTask[i]);
                        threads[i].start();
                    }
                    List<List<Result>> results = Arrays.stream(resultsTask)
                            .map(f -> {
                                try {
                                    return Arrays.asList(f.get());
                                } catch (InterruptedException | ExecutionException e) {
                                    throw new IllegalStateException(e);
                                }
                            })
                            .collect(Collectors.toList());
                    if (currentStrategy.isNeedChangeFirstThread()) {
                        if (firstInteleavingThreadIndex == threadQueue.size() - 1) {
                            if (startScheduleIndex == enumer.size() - 1) {
                                needNextIteration = true;
                            } else {
                                threadQueue = enumer.get(++startScheduleIndex);
                                firstInteleavingThreadIndex = 0;
                            }
                        } else {
                            currentStrategy.setExecutionParameters(threadQueue, new Pair<>(threadQueue.get(firstInteleavingThreadIndex),
                                    threadQueue.get(++firstInteleavingThreadIndex)));
                        }

                    }
                    currentStrategy.prepareInvocation(0, false);
                    // Check correctness& Throw an AssertionError if current execution
                    // is not linearizable and log invalid execution
                    if (!possibleResultsSet.contains(results)) {
                        printExecutionResult(results);
                        printPossibleResults(possibleResultsSet);
                        reportBuilder.result(TestReport.Result.FAILURE);
                        throw new AssertionError("Non-linearizable execution detected, see log for details");
                    }
                }
                currentStrategy.printTraces();
            }
            reportBuilder.result(TestReport.Result.SUCCESS);
        } finally {
            reportBuilder.time(Instant.now().toEpochMilli() - startTime.toEpochMilli());
            writeReportIfNeeded(reportBuilder);
        }
    }

    @Suspendable
    private void checkImplFiber(CTestConfiguration testCfg) throws InterruptedException, ClassNotFoundException, IllegalAccessException, InstantiationException {
        System.err.println("Fibers Enabled:" + SuspendableHelper.isInstrumented(this.getClass()));
        // Fixed thread pool executor to run TestThreadExecution
        ExecutorService pool = Executors.newFixedThreadPool(testCfg.getThreads());
        // Reusable phaser
        final Phaser phaser = new Phaser(testCfg.getThreads());
        // Store start time for counting performance metrics
        Instant startTime = Instant.now();
        // Create report builder
        TestReport.Builder reportBuilder = new TestReport.Builder(testCfg)
                .name(testClassName)
                .strategy("Simple"); // TODO:  Get simpleName of Strategy class
        try {
            System.out.println("Number of iterations: " + testCfg.getIterations());
            System.out.println("Number of invocations per iteration: " + testCfg.getInvocationsPerIteration());

            // Run iterations
            for (int iteration = 1; iteration <= testCfg.getIterations(); iteration++) {
                reportBuilder.incIterations();

                //Set strategy and initialize transformation in classes
                new ThreadYieldStrategy();
                StrategyHolder.setCurrentStrategy(new ThreadYieldStrategy());

                //Create loader, load and instantiate testInstance by this loader
                final ExecutionClassLoader loader = new ExecutionClassLoader(this.getClass().getClassLoader(), testClassName);
//                final ExecutionClassLoader loader = new ExecutionClassLoader(testClassName);
                final Object testInstance = loader.loadClass(testClassName).newInstance();

                List<List<Actor>> actorsPerThread = generateActors(testCfg);
                printIterationHeader(iteration, actorsPerThread);
                // Create TestThreadExecution's
                List<TestThreadExecution> testThreadExecutions = actorsPerThread.stream()
                        .map(actors -> TestThreadExecutionGenerator.create(testInstance, new Phaser(1), actors, false, loader))
//                    .map(actors -> TestThreadExecutionGenerator.create(testInstance, phaser, actors, false, loader))
                        .collect(Collectors.toList());
//                Fiber<Set<List<List<Result>>>> all = new Fiber<Set<List<List<Result>>> >(() -> generatePossibleResults(actorsPerThread, testInstance, loader));
//                all.start();
//
//                Set<List<List<Result>>> possibleResultsSet = null;
//                try {
//                    possibleResultsSet = all.get();
//                } catch (ExecutionException e) {
//                    throw new AssertionError(e);
//                }
                Set<List<List<Result>>> possibleResultsSet = generatePossibleResults(actorsPerThread, testInstance, loader);
                // Run invocations
                for (int invocation = 1; invocation <= testCfg.getInvocationsPerIteration(); invocation++) {
                    reportBuilder.incInvocations();
                    // Reset the state of test
                    invokeReset(testInstance);
                    // Specify waits
                    int maxWait = (int) ((float) invocation * MAX_WAIT / testCfg.getInvocationsPerIteration()) + 1;
                    setWaits(actorsPerThread, testThreadExecutions, maxWait);
                    // Run multithreaded test and get operation results for each thread

                    List<List<Result>> results = testThreadExecutions.stream()
                            .map(c -> new Fiber<>(c::call))
                            .map(Fiber::start)
                            .map(f -> {
                                try {
                                    return Arrays.asList(f.get());
                                } catch (ExecutionException | InterruptedException e) {
                                    throw new IllegalStateException(e);
                                }
                            })
                            .collect(Collectors.toList());
//                    List<List<Result>> results = pool.invokeAll(testThreadExecutions).stream() // get futures
//                        .map(f -> {
//                            try {
//                                return Arrays.asList(f.get()); // wait and get results
//                            } catch (InterruptedException | ExecutionException e) {
//                                throw new IllegalStateException(e);
//                            }
//                        }).collect(Collectors.toList()); // and store results as list
                    // Check correctness& Throw an AssertionError if current execution
                    // is not linearizable and log invalid execution
                    if (!possibleResultsSet.contains(results)) {
                        System.out.println("Iteration Failed");
                        reportBuilder.result(TestReport.Result.FAILURE);
                        throw new AssertionError("Non-linearizable execution detected, see log for details");
                    }
                }
            }
            reportBuilder.result(TestReport.Result.SUCCESS);
        } catch (Throwable a) {
            a.printStackTrace();
        } finally {
            pool.shutdown();
            reportBuilder.time(Instant.now().toEpochMilli() - startTime.toEpochMilli());
            // Print report
            try (Reporter reporter = new Reporter("report")) {
                reporter.report(reportBuilder.build());
            } catch (IOException e) {
                System.out.println("Unable to write report:");
                e.printStackTrace();
            }
        }
    }

    private void writeReportIfNeeded(TestReport.Builder reportBuilder) {
        if (WRITE_REPORT) {
            try (Reporter reporter = new Reporter("report")) {
                reporter.report(reportBuilder.build());
            } catch (IOException e) {
                System.out.println("Unable to write report:");
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                System.out.println("Illegal argument");
                e.printStackTrace();
            }
        }
    }

    //region Printers
    private void printPossibleResults(Set<List<List<Result>>> possibleResultsSet) {
        System.out.println("Possible linearizable executions:");
        possibleResultsSet.forEach(possibleResults -> {
            possibleResults.forEach(System.out::println);
            System.out.println();
        });
    }

    private void printExecutionResult(List<List<Result>> result) {
        System.out.println("Non-linearizable execution:");
        result.forEach(System.out::println);
        System.out.println();
    }

    private void printIterationHeader(int iteration, List<List<Actor>> actorsPerThread) {
        System.out.println("Iteration #" + iteration);
        System.out.println("Actors per thread:");
        actorsPerThread.forEach(System.out::println);
    }
    //endregion

    private Phaser SINGLE_THREAD_PHASER = new Phaser(1);

    private void setWaits(List<List<Actor>> actorsPerThread, List<TestThreadExecution> testThreadExecutions, int maxWait) {
        for (int i = 0; i < testThreadExecutions.size(); i++) {
            TestThreadExecution ex = testThreadExecutions.get(i);
            ex.waits = random.ints(actorsPerThread.get(i).size() - 1, 0, maxWait).toArray();
        }
    }

    private List<Result> executeActors(List<Actor> actors, Object testInstance, ExecutionClassLoader loader) {
        invokeReset(testInstance);
        return Arrays.asList(TestThreadExecutionGenerator.create(testInstance, SINGLE_THREAD_PHASER, actors, false, loader).call());
    }

    private void invokeReset(Object testInstance) {
        // That what was before
        // testStructure.getResetMethod().invoke(testInstance);
        // Now it throws too many exceptions because it get reset method using reflection.
        // Too many reflection. I use reflection even to get ResetMethod which we had in testStructure.
        try {
            testInstance.getClass().getMethod(testStructure.getResetMethod()).invoke(testInstance);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new IllegalStateException("Unable to call method annotated with @Reset", e);
        }
    }
}