package com.devexperts.dxlab.lincheck.strategy;

import co.paralleluniverse.fibers.Suspendable;
import javafx.util.Pair;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
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
    private volatile int currentThread;
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
//        if (Strand.currentStrand().getName().equals(strandName)){
            int th = driver.getCurrentThreadId();
//            Strand th = Strand.currentStrand();
            //ждем, пока можно будет продолжить выполнение
            driver.waitFor(currentThread);
            logger.println("\t\tEnter on SharedRead");
            logger.println("\t\tThread id: " + (th + 1) + " currentID: " + currentThread);
            logger.println("\t\tCurrentLocation id" + location);
            CheckPoint currentPoint = new CheckPoint(location, (th + 1));
            onSharedVariableAccess(currentPoint);
        }
    }

    @Suspendable
    @Override
    public void onSharedVariableWrite(int location) {
        if (driver.getCurrentThreadName().equals(strandName)) {
//        if (Strand.currentStrand().getName().equals(strandName)){
            int th = driver.getCurrentThreadId();
//            Strand th = Strand.currentStrand();
            driver.waitFor(currentThread);
            logger.println("\t\tEnter on SharedWrite");
            logger.println("\t\tThread id: " + (th + 1) + " currentID: " + currentThread);
            logger.println("\t\tCurrentLocation id" + location);
            CheckPoint currentPoint = new CheckPoint(location, (th + 1));
            onSharedVariableAccess(currentPoint);
        }
    }

    @Suspendable
    @Override
    public void endOfThread() {
        if (driver.getCurrentThreadName().equals(strandName)) {
//        if (Strand.currentStrand().getName().equals(strandName)) {
            int th = driver.getCurrentThreadId();
//            Strand th = Strand.currentStrand();
            logger.println("\tEndOfThread " + (th + 1) + "with interleavings:" + wasInterleavings);
            //В конце потока смотрим, если это поток, который не надо было прерывать - выполняем слеующий в расписании
            //иначе - выполняем некоторую логику.
            if (wasInterleavings == 0) {
                if (currentThread == interleavingThreads.getKey()) {
                    firstCheckPoint = null;
                    needInterleave = false;
                    needChangeFirstThread = true;
                }
                int index = helper.threadQueue.indexOf(currentThread) + 1;

                if (index < helper.threadQueue.size()) {
                    currentThread = helper.threadQueue.get(helper.threadQueue.indexOf(currentThread) + 1);
                    driver.switchOnEndOfThread(currentThread);
//                    switchEndOFThread(executionQueue.get(executionQueue.indexOf(currentThread) + 1));
                }
            }
            else if (wasInterleavings == 1) {
                firstCheckPoint = null;
                needInterleave = false;
                if (currentThread == interleavingThreads.getValue()) {
                    currentThread = interleavingThreads.getKey();
                    driver.switchOnEndOfThread(currentThread);
//                    switchEndOFThread(interleavingThreads.getKey());
                    //currentThread = interleavingThreads.getKey();
                }
                else if (currentThread == interleavingThreads.getKey()) {
                    int index = helper.threadQueue.indexOf(currentThread) + 1;
                    if (index < helper.threadQueue.size() - 1) {
                        currentThread = helper.threadQueue.get(helper.threadQueue.indexOf(currentThread) + 2);
                        driver.switchOnEndOfThread(currentThread);
//                        switchEndOFThread(executionQueue.get(executionQueue.indexOf(currentThread) + 2));
                    }
                }
                else {
                    int index = helper.threadQueue.indexOf(currentThread) + 1;
                    if (index < helper.threadQueue.size()) {
                        currentThread = helper.threadQueue.get(helper.threadQueue.indexOf(currentThread) + 1);
                        driver.switchOnEndOfThread(currentThread);
//                        switchEndOFThread(executionQueue.get(executionQueue.indexOf(currentThread) + 1));
                    }
                }
            }
            else if (wasInterleavings == 2) {
                int index = helper.threadQueue.indexOf(currentThread) + 1;
                if (index < helper.threadQueue.size()) {
                    currentThread = helper.threadQueue.get(helper.threadQueue.indexOf(currentThread) + 1);
                    driver.switchOnEndOfThread(currentThread);
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
        if (currentThread == interleavingThreads.getKey() || currentThread == interleavingThreads.getValue()) {
            if (needInterleave) {
                //если нам нужно найти новую точку и находим - переключаемся
                if (wasInterleavings == 0 && firstCheckPoint == null && !passedPaths.containsKey(currentPoint)) {
                    passedPaths.put(currentPoint, new HashSet<>());
                    firstCheckPoint = currentPoint;
                    wasInterleavings++;
                    if (currentThread == interleavingThreads.getKey()) {
                        currentThread = interleavingThreads.getValue();
                        driver.switchThread(currentThread);
//                        changeCurrentThreadTo(interleavingThreads.getValue());
                    } else {
                        currentThread = interleavingThreads.getKey();
                        driver.switchThread(currentThread);
//                        changeCurrentThreadTo(interleavingThreads.getKey());
                    }
                }
                //если нужно найти новую точку и не находим - пропускаем
                else if (wasInterleavings == 0 && firstCheckPoint == null && passedPaths.containsKey(currentPoint)) {
                }
                //если не нужно искать новую точку и мы на ней - переключаемся
                else if (wasInterleavings == 0 && firstCheckPoint != null && currentPoint.equals(firstCheckPoint)) {
                    wasInterleavings++;
                    if (currentThread == interleavingThreads.getKey()) {
                        currentThread = interleavingThreads.getValue();
                        driver.switchThread(currentThread);
//                        changeCurrentThreadTo(interleavingThreads.getValue());
                    } else {
                        currentThread = interleavingThreads.getKey();
                        driver.switchThread(currentThread);
//                        changeCurrentThreadTo(interleavingThreads.getKey());
                    }
                }
                //если не нужно искать новую точку, но мы не на ней - не переключаемся
                else if (wasInterleavings == 0 && firstCheckPoint != null && !currentPoint.equals(firstCheckPoint)) {
                } else if (wasInterleavings == 1 && !passedPaths.get(firstCheckPoint).contains(currentPoint)) {
                    passedPaths.get(firstCheckPoint).add(currentPoint);
                    wasInterleavings++;
                    if (currentThread == interleavingThreads.getKey()) {
                        currentThread = interleavingThreads.getValue();
                        driver.switchThread(currentThread);
//                        changeCurrentThreadTo(interleavingThreads.getValue());
                    } else {
                        currentThread = interleavingThreads.getKey();
                        driver.switchThread(currentThread);
//                        changeCurrentThreadTo(interleavingThreads.getKey());
                    }
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
        currentThread = helper.threadQueue.get(0);
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
        currentThread = helper.threadQueue.get(0);
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

    public void beforeStartIteration() {
        helper = new EnumerationStrategyHelper(2);
    }

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

    public void onEndIteration() {
        printTraces();
    }

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
