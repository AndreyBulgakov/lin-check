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

import co.paralleluniverse.common.util.Exceptions;
import com.devexperts.dxlab.lincheck.report.Reporter;
import com.devexperts.dxlab.lincheck.report.TestReport;
import com.devexperts.dxlab.lincheck.strategy.*;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import sun.misc.URLClassPath;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * TODO documentation
 * TODO logging
 * TODO avoid executions without write operations
 */
public class LinChecker {
    private static final boolean WRITE_REPORT = Boolean.parseBoolean(System.getProperty("lincheck.writeReport", "false"));
    private static final int MAX_WAIT = 1000;

    private final Random random = new Random(0);
    private final String testClassName;
    private final List<CTestConfiguration> testConfigurations;
    private final CTestStructure testStructure;

    private LinChecker(Object testInstance) {
        this.testClassName = testInstance.getClass().getCanonicalName();
        Class<?> testClass = testInstance.getClass();
        this.testConfigurations = CTestConfiguration.getFromTestClass(testClass);
        this.testStructure = CTestStructure.getFromTestClass(testClass);
    }

    /**
     * LinChecker run method. Use LinChecker.check(TestClass.class) in junit test class
     *
     * @param testInstance class that contains CTest
     * @throws AssertionError if find Non-linearizable executions
     */
    public static void check(Object testInstance) throws AssertionError {
        if (CTestConfiguration.getThreadType().equals(ExecutionsStrandPool.StrandType.FIBER)) {
            checkFiber(testInstance);
        }else {
            new LinChecker(testInstance).check();
        }
    }

    /**
     * @throws AssertionError if atomicity violation is detected
     */
    private void check() throws AssertionError {
        Consumer<CTestConfiguration> checkFunction = CTestConfiguration.isParallelEnabled()?
                this::checkImplParallel : this::checkImpl;
        testConfigurations.forEach(checkFunction);
    }

    static void checkFiber(Object testInstance){
        try {
            // Get current URLs from parrent classLoader
            Field ucp = URLClassLoader.class.getDeclaredField("ucp");
            ucp.setAccessible(true);
            URL[] classLoaderUrls = ((URLClassPath) ucp.get(LinChecker.class.getClassLoader())).getURLs();
            // Loading instruments
            QuasarLoader urlClassLoader = new QuasarLoader(classLoaderUrls);
            Thread.currentThread().setContextClassLoader(urlClassLoader);
            // Log
//          helper.setLog(true, true);
            Class<?> instrumentedLincheckClass = urlClassLoader.loadClass("com.devexperts.dxlab.lincheck.LinChecker");
            Class<?> instrumentedTestInstance = urlClassLoader.loadClass(testInstance.getClass().getName());
            Object newInstance = instrumentedTestInstance.newInstance();

            Constructor linCheckConstructor = instrumentedLincheckClass.getDeclaredConstructor(Object.class);
            linCheckConstructor.setAccessible(true);
            Object linCheckInstance = linCheckConstructor.newInstance(newInstance);
            Method checkMethdod = instrumentedLincheckClass.getDeclaredMethod("check");
            checkMethdod.setAccessible(true);
            checkMethdod.invoke(linCheckInstance);
//            Method m = instrumentedLincheckClass.getMethod("check", Object.class);
//            m.invoke(null, newInstance);
        } catch (NoSuchMethodException | InstantiationException | InvocationTargetException | NoSuchFieldException | ClassNotFoundException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    //region Generators
    private List<Actor> generateActorsForThread(CTestConfiguration.TestThreadConfiguration threadCfg) {
        int actorsInThread = threadCfg.minActors + random.nextInt(threadCfg.maxActors - threadCfg.minActors + 1);
        return random.ints(actorsInThread, 0, testStructure.getActorGenerators().size()) // random indexes
                .mapToObj(i -> testStructure.getActorGenerators().get(i)) // random actor generators
                .map(ActorGenerator::generate) // generate actors
                .collect(Collectors.toList()); // return as list
    }

    private synchronized List<List<Actor>> generateActors(CTestConfiguration testConfiguration) {
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
                                                            CleanClassLoader loader) {
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

    private void checkImpl(CTestConfiguration testCfg) {
        // Fixed thread pool executor to run TestThreadExecution
        // Store start time for counting performance metrics
        Instant startTime = Instant.now();
        // Create report builder
        TestReport.Builder reportBuilder = new TestReport.Builder(testCfg)
                .name(testClassName);
        try {
            // Reusable phaser
            final Phaser phaser = new Phaser(1);
            //Set strategy and initialize transformation in classes
            for (int iteration = 1; iteration <= testCfg.getIterations(); iteration++) {
                ExecutionsStrandPool strandPool = new ExecutionsStrandPool(testCfg.getThreadType(), iteration);
                Driver driver = new StrandDriver(strandPool);
                EnumerationStrategy currentStrategy = new EnumerationStrategy(driver);

                DummyStrategy dummyStrategy = new DummyStrategy();
                CleanClassLoader cleanClassLoader = new CleanClassLoader(getClass().getClassLoader());
                Object cleanInstance = cleanClassLoader.loadClass(testClassName).newInstance();

                reportBuilder.strategy(currentStrategy.getClass().getSimpleName().replace("Strategy", ""));

                currentStrategy.beforeStartIteration(testCfg.getThreads());
                currentStrategy.onStartIteration();

                reportBuilder.incIterations();

                //Create loader, load and instantiate testInstance by this loader
                final ExecutionClassLoader loader = new ExecutionClassLoader(getClass().getClassLoader());
                final Object testInstance = loader.loadClass(testClassName).newInstance();

                List<List<Actor>> actorsPerThread = generateActors(testCfg);
                printIterationHeader(iteration, actorsPerThread);
                // Create TestThreadExecution's
                List<TestThreadExecution> testThreadExecutions = actorsPerThread.stream()
                        .map(actors -> TestThreadExecutionGenerator.create(testInstance, new Phaser(1), actors, false, loader))
                        .collect(Collectors.toList());

                StrategyHolder.setCurrentStrategy(iteration,dummyStrategy);
                //Generate possible results
                Set<List<List<Result>>> possibleResultsSet = generatePossibleResults(actorsPerThread, cleanInstance, cleanClassLoader);
                StrategyHolder.setCurrentStrategy(iteration, currentStrategy);

                // Run invocations
                for (int invocation = 1; invocation <= testCfg.getInvocationsPerIteration() && !currentStrategy.isNeedStopIteration(); invocation++) {
                    currentStrategy.onStartInvocation(iteration, invocation);
                    reportBuilder.incInvocations();
                    // Reset the state of test
                    invokeReset(testInstance);

                    List<List<Result>> results = strandPool
                            .add(testThreadExecutions)
                            .invokeAll().stream()
                            .map(f -> {
                                try {
                                    return Arrays.asList(f.get());
                                } catch (ExecutionException | InterruptedException e) {
                                    throw new IllegalStateException(e);
                                }
                            })
                            .collect(Collectors.toList());
                    strandPool.clear();
                    currentStrategy.onEndInvocation();
                    // Check correctness& Throw an AssertionError if current execution
                    // is not linearizable and log invalid execution
                    if (!possibleResultsSet.contains(results)) {
                        printExecutionResult(results);
                        printPossibleResults(possibleResultsSet);
                        reportBuilder.result(TestReport.Result.FAILURE);
                        throw new AssertionError("Non-linearizable execution detected, see log for details");
                    }
                }

            }
            reportBuilder.result(TestReport.Result.SUCCESS);
        } catch (Throwable e) {
            e.printStackTrace();
            throw Exceptions.rethrow(e);
        } finally {
            reportBuilder.time(Instant.now().toEpochMilli() - startTime.toEpochMilli());
            writeReportIfNeeded(reportBuilder);
        }
    }

    private void checkImplParallel(CTestConfiguration testCfg)  {
        // Store start time for counting performance metrics
        long startTime = Instant.now().toEpochMilli();
        // Create report builder
        try {
            final Object lock = new Object();
            final IterationListener listener = new IterationListener(lock, testCfg, testClassName, startTime);
            final int poolCount = Runtime.getRuntime().availableProcessors();

            //Iteration queue
            LinkedBlockingQueue<Runnable> queue = new LinkedBlockingQueue<Runnable>(){
                private AtomicInteger iteration = new AtomicInteger(0);
                private final int maxIterations = testCfg.getIterations();
                @Override
                public Runnable take() throws InterruptedException {
                    if (isEmpty()) {
                        int i = iteration.incrementAndGet();
                        if (i <= maxIterations) put(() -> iteration(i, testCfg, listener));
                    }
                    return super.take();
                }
            };

            ThreadPoolExecutor service = new ThreadPoolExecutor(poolCount, poolCount, 0L, TimeUnit.MILLISECONDS,queue);

            //Create ThreadFactory with handler
            final Thread.UncaughtExceptionHandler handler = (t, e) -> {
                e.printStackTrace();
                synchronized (lock){lock.notify();}
            };
            ThreadFactory factory = new ThreadFactoryBuilder().setUncaughtExceptionHandler(handler).build();
            service.setThreadFactory(factory);

            //Start pool with queue
            service.prestartAllCoreThreads();

            //Wait until catch exceptions or all iterations finished
            synchronized (lock){
                lock.wait();
            }
            if (listener.isNonLinearizable())
                throw new AssertionError("Non-linearizable execution detected, see log for details");

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            TestReport.Builder reportBuilder = new TestReport.Builder(testCfg).name(testClassName);
            reportBuilder.time(Instant.now().toEpochMilli() - startTime);
            writeReportIfNeeded(reportBuilder);
        }
    }

    private void iteration(int iteration, CTestConfiguration testCfg, IterationListener listner) {
        try {
            ExecutionsStrandPool strandPool = new ExecutionsStrandPool(testCfg.getThreadType(), iteration);
            Driver driver = new StrandDriver(strandPool);
            EnumerationStrategy currentStrategy = new EnumerationStrategy(driver);

            DummyStrategy dummyStrategy = new DummyStrategy();
            CleanClassLoader cleanClassLoader = new CleanClassLoader(getClass().getClassLoader());
            Object cleanInstance = cleanClassLoader.loadClass(testClassName).newInstance();

            currentStrategy.beforeStartIteration(testCfg.getThreads());
            currentStrategy.onStartIteration();

            //Create loader, load and instantiate testInstance by this loader
            final ExecutionClassLoader loader = new ExecutionClassLoader(getClass().getClassLoader());
            final Object testInstance = loader.loadClass(testClassName).newInstance();

            List<List<Actor>> actorsPerThread = generateActors(testCfg);
            // Create TestThreadExecution's
            List<TestThreadExecution> testThreadExecutions = actorsPerThread.stream()
                    .map(actors -> TestThreadExecutionGenerator.create(testInstance, new Phaser(1), actors, false, loader))
                    .collect(Collectors.toList());

            StrategyHolder.setCurrentStrategy(iteration,dummyStrategy);
            //Generate possible results
            Set<List<List<Result>>> possibleResultsSet = generatePossibleResults(actorsPerThread, cleanInstance, cleanClassLoader);
            StrategyHolder.setCurrentStrategy(iteration, currentStrategy);

            // Run invocations
            for (int invocation = 1; invocation <= testCfg.getInvocationsPerIteration() && !currentStrategy.isNeedStopIteration(); invocation++) {
                currentStrategy.onStartInvocation(iteration, invocation);
                // Reset the state of test
                invokeReset(testInstance);

                List<List<Result>> results = strandPool
                        .add(testThreadExecutions)
                        .invokeAll().stream()
                        .map(f -> {
                            try {
                                return Arrays.asList(f.get());
                            } catch (ExecutionException | InterruptedException e) {
                                throw new IllegalStateException(e);
                            }
                        })
                        .collect(Collectors.toList());
                strandPool.clear();
                currentStrategy.onEndInvocation();
                // Check correctness & Throw an AssertionError if current execution
                // is not linearizable and log invalid execution
                if (!possibleResultsSet.contains(results)) {
                    System.out.println("IterationNum" + iteration);
                    printExecutionResult(results);
                    printPossibleResults(possibleResultsSet);
                    listner.foundNonLinearizable(currentStrategy, iteration, invocation);
                }
            }
            currentStrategy.onEndIteration();
            listner.onEndIteration();
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            throw new IllegalStateException(e);
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

    private synchronized void printIterationHeader(int iteration, List<List<Actor>> actorsPerThread) {
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

    private List<Result> executeActors(List<Actor> actors, Object testInstance, CleanClassLoader loader) {
        invokeReset(testInstance);
        return Arrays.asList(TestThreadExecutionGenerator.create(testInstance, new Phaser(1), actors, false, loader).call());
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