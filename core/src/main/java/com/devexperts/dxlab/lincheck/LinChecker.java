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

import com.devexperts.dxlab.lincheck.strategy.ConsumeCPUStrategy;
import com.devexperts.dxlab.lincheck.strategy.StrategyHolder;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Phaser;
import java.util.stream.Collectors;

/**
 * TODO documentation
 * TODO logging
 * TODO avoid executions without write operations
 */
public class LinChecker {
    private static final int MAX_WAIT = 1000;

    private final Random random = new Random();
    private final String testClassName;
    private final List<CTestConfiguration> testConfigurations;
    private final CTestStructure testStructure;

    private LinChecker(Class testClass) {
        this.testClassName = testClass.getCanonicalName();
        this.testConfigurations = CTestConfiguration.getFromTestClass(testClass);
        this.testStructure = CTestStructure.getFromTestClass(testClass);
    }

    private LinChecker(Object testInstance) {
        this.testClassName = testInstance.getClass().getCanonicalName();
        Class<?> testClass = testInstance.getClass();
        this.testConfigurations = CTestConfiguration.getFromTestClass(testClass);
        this.testStructure = CTestStructure.getFromTestClass(testClass);
    }

    /**
     * LinChecker run method. Use LinChecker.check(TestClass.class) in junit test class
     * @param testClass class that contains CTest
     * @throws AssertionError if find Non-linearizable executions
     */
    public static void check(Class testClass) throws AssertionError {
            new LinChecker(testClass).check();
    }

    /**
     * LinChecker run method. Use LinChecker.check(this) in junit test class
     * @param testInstance object that contains CTest
     * @throws AssertionError if find Non-linearizable executions
     */
    public static void check(Object testInstance) throws AssertionError {
            new LinChecker(testInstance).check();

    }

    /**
     * @throws AssertionError if atomicity violation is detected
     */
    private void check() throws AssertionError {
        testConfigurations.forEach((testConfiguration) -> {
            try {
                checkImpl(testConfiguration);
            } catch (InterruptedException | ClassNotFoundException | IllegalAccessException | InstantiationException e) {
                // TODO checkImpl should catch these exceptions
                throw new IllegalStateException(e);
            }
        });
    }

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

    private void printActorsPerThread(List<List<Actor>> actorsPerThread) {
        System.out.println("Actors per thread:");
        actorsPerThread.forEach(System.out::println);
    }

    private List<List<Actor>> generateAllLinearizableExecutions(List<List<Actor>> actorsPerThread) {
        List<List<Actor>> executions = new ArrayList<>();
        generateLinearizableExecutions0(executions, actorsPerThread, new ArrayList<>(), new int[actorsPerThread.size()],
            actorsPerThread.stream().mapToInt(List::size).sum());
        return executions;
    }

    private void generateLinearizableExecutions0(List<List<Actor>> executions, List<List<Actor>> actorsPerThread,
        ArrayList<Actor> currentExecution, int[] indexes, int length)
    {
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

    private void checkImpl(CTestConfiguration testCfg) throws InterruptedException, ClassNotFoundException, IllegalAccessException, InstantiationException {
        // Fixed thread pool executor to run TestThreadExecution
        ExecutorService pool = Executors.newFixedThreadPool(testCfg.getThreads());
        try {
            // Reusable phaser
            final Phaser phaser = new Phaser(testCfg.getThreads());
            // Run iterations
            for (int iteration = 1; iteration <= testCfg.getIterations(); iteration++) {

                //Set strategy and initialize transformation in classes
                StrategyHolder.setCurrentStrategy(new ConsumeCPUStrategy(100));
                final ExecutionClassLoader loader = new ExecutionClassLoader(testClassName);
                final Object testInstance = loader.loadClass(testClassName).newInstance();

                System.out.println("= Iteration " + iteration + " / " + testCfg.getIterations() + " =");
                // Generate actors and print them to console
                List<List<Actor>> actorsPerThread = generateActors(testCfg);
                printActorsPerThread(actorsPerThread);
                // Create TestThreadExecution's
                List<TestThreadExecution> testThreadExecutions = actorsPerThread.stream()
                    .map(actors -> TestThreadExecutionGenerator.create(testInstance, phaser, actors, false, loader))
                    .collect(Collectors.toList());
                // Generate all possible results
                Set<List<List<Result>>> possibleResultsSet = generateAllLinearizableExecutions(actorsPerThread).stream()
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
                // Run invocations
                for (int invocation = 0; invocation < testCfg.getInvocationsPerIteration(); invocation++) {
                    // Reset the state of test
                    invokeReset(testInstance);
                    // Specify waits
                    int maxWait = (int) ((float) invocation * MAX_WAIT / testCfg.getInvocationsPerIteration()) + 1;
                    for (int i = 0; i < testThreadExecutions.size(); i++) {
                        TestThreadExecution ex = testThreadExecutions.get(i);
                        ex.waits = random.ints(actorsPerThread.get(i).size() - 1, 0, maxWait).toArray();
                    }
                    // Run multithreaded test and get operation results for each thread
                    List<List<Result>> results = pool.invokeAll(testThreadExecutions).stream() // get futures
                        .map(f -> {
                            try {
                                return Arrays.asList(f.get()); // wait and get results
                            } catch (InterruptedException | ExecutionException e) {
                                throw new IllegalStateException(e);
                            }
                        }).collect(Collectors.toList()); // and store results as list
                    // Check correctness& Throw an AssertionError if current execution
                    // is not linearizable and log invalid execution
                    if (!possibleResultsSet.contains(results)) {
                        System.out.println("\nNon-linearizable execution:");
                        results.forEach(System.out::println);
                        System.out.println("\nPossible linearizable executions:");
                        possibleResultsSet.forEach(possibleResults -> {
                            possibleResults.forEach(System.out::println);
                            System.out.println();
                        });
                        throw new AssertionError("Non-linearizable execution detected, see log for details");
                    }
                }
            }
        } finally {
            pool.shutdown();
        }
    }

    private Phaser SINGLE_THREAD_PHASER = new Phaser(1);

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