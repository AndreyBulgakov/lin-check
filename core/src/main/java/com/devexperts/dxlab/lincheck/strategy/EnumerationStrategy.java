package com.devexperts.dxlab.lincheck.strategy;

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

import co.paralleluniverse.fibers.SuspendExecution;
import co.paralleluniverse.fibers.Suspendable;
import co.paralleluniverse.strands.Strand;
import javafx.util.Pair;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.nio.file.StandardOpenOption.APPEND;

/**
 * Strategy
 */
public class EnumerationStrategy implements Strategy {
    private Map<CheckPoint, Set<CheckPoint>> passedPaths = new HashMap<>();
    private CheckPoint firstCheckPoint;
    PrintStream logger = getLogger();
    private AtomicInteger currentThreadNum;
    private volatile int wasInterleavings = 0;
    private volatile Pair<Integer, Integer> interleavingThreads;
    private volatile boolean needInterleave = true;
    private volatile boolean needChangeFirstThread = false;
    private volatile List<CheckPoint> history = new ArrayList<>();
    private final String strandName = "LinCheckStrand";
    private EnumerationStrategyHelper helper;
    private Driver driver;

    public EnumerationStrategy(Driver driver) {
        this.driver = driver;
    }
    
    //region Logic
    @Suspendable
    @Override
    public void onSharedVariableRead(int location) {
        if (driver.getCurrentThreadName().equals(strandName)) {
            int th = driver.getCurrentThreadId();
            //ждем, пока можно будет продолжить выполнение
            while (th + 1 != currentThreadNum.get()) {}
            //driver.waitFor(currentThread);
            logger.println("\t\tEnter on SharedRead");
            logger.println("\t\tThread id: " + (th + 1) + " currentID: " + currentThreadNum.get());
            logger.println("\t\tCurrentLocation id" + location);
            CheckPoint currentPoint = new CheckPoint(location, (th + 1));
            onSharedVariableAccess(currentPoint);
            while (th + 1 != currentThreadNum.get()) {}
        }
    }

    @Suspendable
    @Override
    public void onSharedVariableWrite(int location) {
        if (driver.getCurrentThreadName().equals(strandName)) {
            int th = driver.getCurrentThreadId();
            //driver.waitFor(currentThread);
            while (th + 1 != currentThreadNum.get()) {}
            logger.println("\t\tEnter on SharedWrite");
            logger.println("\t\tThread id: " + (th + 1) + " currentID: " + currentThreadNum.get());
            logger.println("\t\tCurrentLocation id" + location);
            CheckPoint currentPoint = new CheckPoint(location, (th + 1));
            onSharedVariableAccess(currentPoint);
            while (th + 1 != currentThreadNum.get()) {}
        }
    }

    @Suspendable
    @Override
    public void startOfThread() {
        if (driver.getCurrentThreadName().equals(strandName)) {
            logger.println("park thread with id" + (driver.getCurrentThreadId() + 1));
            try {
                while ((driver.getCurrentThreadId() + 1) != currentThreadNum.get()) {
                    Strand.park();
                }
            } catch (SuspendExecution suspendExecution) {
                suspendExecution.printStackTrace();
            }
        }
    }

    @Suspendable
    @Override
    public void endOfThread() {
        if (driver.getCurrentThreadName().equals(strandName)) {
            int th = driver.getCurrentThreadId();
//            Strand th = Strand.currentStrand();
            logger.println("\tEndOfThread " + (th + 1) + "with interleavings:" + wasInterleavings);
            //В конце потока смотрим, если это поток, который не надо было прерывать - выполняем слеующий в расписании
            //иначе - выполняем некоторую логику.
            if (wasInterleavings == 0) {
                if (currentThreadNum.get() == interleavingThreads.getKey()) {
                    firstCheckPoint = null;
                    needInterleave = false;
                    needChangeFirstThread = true;
                }
                int index = helper.threadQueue.indexOf(currentThreadNum.get()) + 1;

                if (index < helper.threadQueue.size()) {

                    currentThreadNum.set(helper.threadQueue.get(helper.threadQueue.indexOf(currentThreadNum.get()) + 1));
                    driver.switchOnEndOfThread(currentThreadNum);
//                    switchEndOFThread(executionQueue.get(executionQueue.indexOf(currentThread) + 1));
                }
            }
            else if (wasInterleavings == 1) {
                firstCheckPoint = null;
                needInterleave = false;
                if (currentThreadNum.get() == interleavingThreads.getValue()) {
                    currentThreadNum.set(interleavingThreads.getKey());
                    driver.switchOnEndOfThread(currentThreadNum);
//                    switchEndOFThread(interleavingThreads.getKey());
                    //currentThread = interleavingThreads.getKey();
                }
                else if (currentThreadNum.get() == interleavingThreads.getKey()) {
                    int index = helper.threadQueue.indexOf(currentThreadNum.get()) + 1;
                    if (index < helper.threadQueue.size() - 1) {
                        currentThreadNum.set(helper.threadQueue.get(helper.threadQueue.indexOf(currentThreadNum.get()) + 2));
                        driver.switchOnEndOfThread(currentThreadNum);
//                        switchEndOFThread(executionQueue.get(executionQueue.indexOf(currentThread) + 2));
                    }
                }
                else {
                    int index = helper.threadQueue.indexOf(currentThreadNum.get()) + 1;
                    if (index < helper.threadQueue.size()) {
                        currentThreadNum.set(helper.threadQueue.get(helper.threadQueue.indexOf(currentThreadNum.get()) + 1));
                        driver.switchOnEndOfThread(currentThreadNum);
//                        switchEndOFThread(executionQueue.get(executionQueue.indexOf(currentThread) + 1));
                    }
                }
            }
            else if (wasInterleavings == 2) {
                int index = helper.threadQueue.indexOf(currentThreadNum.get()) + 1;
                if (index < helper.threadQueue.size()) {
                    currentThreadNum.set(helper.threadQueue.get(helper.threadQueue.indexOf(currentThreadNum.get()) + 1));
                    driver.switchOnEndOfThread(currentThreadNum);
//                    switchEndOFThread(executionQueue.get(executionQueue.indexOf(currentThread) + 1));
                }
            }
            else {
                logger.println("something else");
            }
        }
    }

    /**
     * Method contains logic for interleaving thread
     * @param currentPoint pair locationId, threadID
     */
    @Suspendable
    private void onSharedVariableAccess(CheckPoint currentPoint) {
        if (currentThreadNum.get() == interleavingThreads.getKey() || currentThreadNum.get() == interleavingThreads.getValue()) {
            if (needInterleave) {
                //если нам нужно найти новую точку и находим - переключаемся
                if (wasInterleavings == 0 && firstCheckPoint == null && !passedPaths.containsKey(currentPoint)) {
                    passedPaths.put(currentPoint, new HashSet<>());
                    firstCheckPoint = currentPoint;
                    wasInterleavings++;
                    if (currentThreadNum.get() == interleavingThreads.getKey()) {
                        currentThreadNum.set(interleavingThreads.getValue());
                        driver.switchThread(currentThreadNum);
//                        changeCurrentThreadTo(interleavingThreads.getValue());
                    } else {
                        currentThreadNum.set(interleavingThreads.getKey());
                        driver.switchThread(currentThreadNum);
                    }
                }
                //если нужно найти новую точку и не находим - пропускаем
                else if (wasInterleavings == 0 && firstCheckPoint == null && passedPaths.containsKey(currentPoint)) {
                }
                //если не нужно искать новую точку и мы на ней - переключаемся
                else if (wasInterleavings == 0 && firstCheckPoint != null && currentPoint.equals(firstCheckPoint)) {
                    wasInterleavings++;
                    if (currentThreadNum.get() == interleavingThreads.getKey()) {
                        currentThreadNum.set(interleavingThreads.getValue());
                        driver.switchThread(currentThreadNum);
//                        changeCurrentThreadTo(interleavingThreads.getValue());
                    } else {
                        currentThreadNum.set(interleavingThreads.getKey());
                        driver.switchThread(currentThreadNum);
//                        changeCurrentThreadTo(interleavingThreads.getKey());
                    }
                }
                //если не нужно искать новую точку, но мы не на ней - не переключаемся
                else if (wasInterleavings == 0 && firstCheckPoint != null && !currentPoint.equals(firstCheckPoint)) {
                }
                else if (wasInterleavings == 1 && !passedPaths.get(firstCheckPoint).contains(currentPoint)) {
                    passedPaths.get(firstCheckPoint).add(currentPoint);
                    wasInterleavings++;
                    if (currentThreadNum.get() == interleavingThreads.getKey()) {
                        currentThreadNum.set(interleavingThreads.getValue());
                        driver.switchThread(currentThreadNum);
//                        changeCurrentThreadTo(interleavingThreads.getValue());
                    } else {
                        currentThreadNum.set(interleavingThreads.getKey());
                        driver.switchThread(currentThreadNum);
//                        changeCurrentThreadTo(interleavingThreads.getKey());
                    }
                }
                else if (wasInterleavings == 2) {
                    logger.println("was Interleavings = " + wasInterleavings);
                }
            }
        }
    }
    //endregion

    //region Logging
    /**
     * Method for printing information about current invocation
     * @param iteration - current iteration
     * @param invocation - current invocation
     */
    public void printHeader(int iteration, int invocation) {
        logger.println("Iteration №" + iteration + " Invocation №" + invocation + " FirstCheckPoint" + firstCheckPoint);
    }

    public static PrintStream getLogger() {
        try {
            Path p = Paths.get(System.getProperty("user.dir"), "log");
            if (Files.exists(p)) {
                return new PrintStream(Files.newOutputStream(p, APPEND));
            } else {
                return new PrintStream(Files.newOutputStream(p));
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Print all checked points to log file
     */
    public void printTraces() {
        passedPaths.forEach((a,b) -> logger.println(a + " with set: " + b));
    }
    //endregion

    //region Helpers
    /**
     * Clear information about previous running
     * @param firstThread
     * @param needChangeFirstThread
     */
    public void prepareInvocation(int firstThread, boolean needChangeFirstThread){
        needInterleave = true;
        currentThreadNum = new AtomicInteger(helper.threadQueue.get(0));
        this.needChangeFirstThread = needChangeFirstThread;
        wasInterleavings = 0;
    }

    public boolean isNeedChangeFirstThread() {
        return needChangeFirstThread;
    }

    /**
     * Method print checked point to log file and clear all information about previous iteration
     */
    public void prepareIteration() {
        printTraces();
        firstCheckPoint = null;
        passedPaths = new HashMap<>();
        prepareInvocation(1, false);
    }

    public void setExecutionParameters(List<Integer> queue, Pair<Integer, Integer> interleavedThreads) {
        currentThreadNum = new AtomicInteger(helper.threadQueue.get(0));
        this.interleavingThreads = interleavedThreads;
        passedPaths = new HashMap<>();
        logger.println("Current queue of threads execution" + queue);
        logger.println("Current interleaving threads id's" + interleavedThreads);
    }

    public static ArrayList<Pair<Integer, Integer>> generateInterleavedPairs(int threadNumber) {
        ArrayList<Pair<Integer, Integer>> pairList = new ArrayList<>();
        for (int i = 1; i < threadNumber; i++) {
            for (int j = i+1; j <= threadNumber; j++) {
                pairList.add(new Pair<>(i, j));
            }
        }
        return pairList;
    }
    //endregion

    @Override
    public void beforeStartIteration(int threadNumber) {
        helper = new EnumerationStrategyHelper(threadNumber);
    }

    @Override
    public void onStartIteration() {
        helper.firstInteleavingThreadIndex = 0;
        helper.startScheduleIndex = 0;
        helper.threadQueue = helper.queueThreadExecutions.get(helper.startScheduleIndex);
        setExecutionParameters(helper.threadQueue, new Pair<>(helper.threadQueue.get(helper.firstInteleavingThreadIndex),
                helper.threadQueue.get(++helper.firstInteleavingThreadIndex)));
        helper.needNextIteration = false;
        prepareIteration();
    }

    public void onStartInvocation(int iteration, int invocation) {
        printHeader(iteration, invocation);
    }

    public void onEndInvocation() {
        if (isNeedChangeFirstThread()) {
            if (helper.firstInteleavingThreadIndex == helper.threadQueue.size() - 1) {
                if (helper.startScheduleIndex == helper.queueThreadExecutions.size() - 1) {
                    helper.needNextIteration = true;
                }
                else {
                    helper.threadQueue = helper.queueThreadExecutions.get(++helper.startScheduleIndex);
                    helper.firstInteleavingThreadIndex = 0;
                    setExecutionParameters(helper.threadQueue, new Pair<>(helper.threadQueue.get(helper.firstInteleavingThreadIndex),
                            helper.threadQueue.get(++helper.firstInteleavingThreadIndex)));
                }
            } else {
                setExecutionParameters(helper.threadQueue, new Pair<>(helper.threadQueue.get(helper.firstInteleavingThreadIndex),
                        helper.threadQueue.get(++helper.firstInteleavingThreadIndex)));
            }

        }
        prepareInvocation(0, false);
    }

    @Override
    public void onEndIteration() {
        printTraces();
    }

    @Override
    public boolean isNeedStopIteration() {
        return helper.needNextIteration;
    }

    /**
     * Class describing Point with respect to location and thread id`s
     */
    private class CheckPoint{
        private final int location;
        private final int threadId;

        public CheckPoint(int location, int threadId) {
            this.location = location;
            this.threadId = threadId;
        }

        @Override
        public String toString() {
            return "CheckPoint<" +
                    location +
                    ", " + threadId + '>';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            CheckPoint that = (CheckPoint) o;

            if (location != that.location) return false;
            return threadId == that.threadId;

        }

        @Override
        public int hashCode() {
            int result = location;
            result = 31 * result + threadId;
            return result;
        }
    }

    private static class EnumerationStrategyHelper {
        //список возможных запусков
        public List<List<Integer>> queueThreadExecutions;

        //индекс потока, в котором делаем прерывание
        public int firstInteleavingThreadIndex;
        public int startScheduleIndex;
        public boolean needNextIteration;

        //первый возможный запуск
        List<Integer> threadQueue;

        public EnumerationStrategyHelper(int threadNumber) {
            List<Integer> list = IntStream.range(1, threadNumber + 1).boxed().collect(Collectors.toList());
            queueThreadExecutions = threadPermutations(list);
        }

        public static List<List<Integer>> threadPermutations(List<Integer> list) {

            if (list.size() == 0) {
                List<List<Integer>> result = new ArrayList<List<Integer>>();
                result.add(new ArrayList<Integer>());
                return result;
            }

            List<List<Integer>> returnMe = new ArrayList<List<Integer>>();
            Integer firstElement = list.remove(0);
            List<List<Integer>> recursiveReturn = threadPermutations(list);
            for (List<Integer> li : recursiveReturn) {
                for (int index = 0; index <= li.size(); index++) {
                    List<Integer> temp = new ArrayList<Integer>(li);
                    temp.add(index, firstElement);
                    returnMe.add(temp);
                }
            }
            return returnMe;
        }

    }

}
