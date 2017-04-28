package com.devexperts.dxlab.lincheck.strategy;

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
    private Map<ArrayList<CheckPoint>, Set<ArrayList<CheckPoint>>> passedPaths;

    //точка, в которую мы попадем после прерывания
    private CheckPoint closeWindow;
    //точка, после которой мы прерываемся
    private CheckPoint openWindowPoint;
    PrintStream logger = getLogger();
    private AtomicInteger currentThreadNum;
    private volatile int wasInterleavings;
    private volatile Pair<Integer, Integer> interleavingThreads;
    private volatile boolean needInterleave;
    private volatile boolean needChangeFirstThread;
    private volatile List<CheckPoint> history;
    private final String strandName = "LinCheckStrand";
    private EnumerationStrategyHelper helper;
    private Driver driver;

    public EnumerationStrategy(Driver driver) {
        this.driver = driver;
        wasInterleavings = 0;
        history = new ArrayList<>();
        needInterleave = true;
        needChangeFirstThread = false;
        passedPaths = new HashMap<>();
    }
    
    //region Logic
    @Suspendable
    @Override
    public void onSharedVariableRead(int location) {
        if (driver.getCurrentThreadName().equals(strandName)) {
            int th = driver.getCurrentThreadId();
            //если история не пуста, то, возможно, стоит переключиться
            logger.println("\t\tEnter on SharedRead");
            logger.println("\t\tThread id: " + (th + 1) + " currentID: " + currentThreadNum.get());
            logger.println("\t\tCurrentLocation id" + location);
            CheckPoint currentPoint = new CheckPoint(location, (th + 1), AccessType.READ);
            if (wasInterleavings < 2 && history.size() > 0){
                CheckPoint prevoiusPoint = history.get(history.size() - 1);
                onSharedVariableAccess(currentPoint, prevoiusPoint);
            }
            history.add(currentPoint);
            logger.println("\tEnd Of Read" + location);
        }
    }

    @Suspendable
    @Override
    public void onSharedVariableWrite(int location) {
        if (driver.getCurrentThreadName().equals(strandName)) {
            int th = driver.getCurrentThreadId();
            logger.println("\t\tEnter on SharedWrite");
            logger.println("\t\tThread id: " + (th + 1) + " currentID: " + currentThreadNum.get());
            logger.println("\t\tCurrentLocation id" + location);
            CheckPoint currentPoint = new CheckPoint(location, (th + 1), AccessType.WRITE);
            if (wasInterleavings < 2 && history.size() > 0){
                CheckPoint prevoiusPoint = history.get(history.size() - 1);
                onSharedVariableAccess(currentPoint, prevoiusPoint);
            }
            history.add(currentPoint);
            logger.println("\tEnd Of Write" + location);
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
                    openWindowPoint = null;
                    needInterleave = false;
                    needChangeFirstThread = true;
                }
                int index = helper.threadQueue.indexOf(currentThreadNum.get()) + 1;

                if (index < helper.threadQueue.size()) {

                    currentThreadNum.set(helper.threadQueue.get(helper.threadQueue.indexOf(currentThreadNum.get()) + 1));
                    driver.switchOnEndOfThread(currentThreadNum);
                }
            }
            else if (wasInterleavings == 1) {
                openWindowPoint = null;
                needInterleave = false;

                if (currentThreadNum.get() == interleavingThreads.getValue()) {
                    currentThreadNum.set(interleavingThreads.getKey());
                    driver.switchOnEndOfThread(currentThreadNum);
                }
                else if (currentThreadNum.get() == interleavingThreads.getKey()) {
                    int index = helper.threadQueue.indexOf(currentThreadNum.get()) + 1;
                    if (index < helper.threadQueue.size() - 1) {
                        currentThreadNum.set(helper.threadQueue.get(helper.threadQueue.indexOf(currentThreadNum.get()) + 2));
                        driver.switchOnEndOfThread(currentThreadNum);
                    }
                }
                else {
                    int index = helper.threadQueue.indexOf(currentThreadNum.get()) + 1;
                    if (index < helper.threadQueue.size()) {
                        currentThreadNum.set(helper.threadQueue.get(helper.threadQueue.indexOf(currentThreadNum.get()) + 1));
                        driver.switchOnEndOfThread(currentThreadNum);
                    }
                }
            }
            else if (wasInterleavings == 2) {
                int index = helper.threadQueue.indexOf(currentThreadNum.get()) + 1;
                if (index < helper.threadQueue.size()) {
                    currentThreadNum.set(helper.threadQueue.get(helper.threadQueue.indexOf(currentThreadNum.get()) + 1));
                    driver.switchOnEndOfThread(currentThreadNum);
                }
            }
            else {
                logger.println("something else");
            }
        }
    }

    /**
     * Method contains logic for interleaving thread
     * @param checkPoint pair locationId, threadID
     */
    @Suspendable
    private void onSharedVariableAccess(CheckPoint checkPoint, CheckPoint previousPoint) {
        if (currentThreadNum.get() == interleavingThreads.getKey() || currentThreadNum.get() == interleavingThreads.getValue()) {
            if (needInterleave) {
                //если ещё не было прерываний
                if (wasInterleavings == 0){
                    //если точка не задана
                    if (openWindowPoint == null){
                        //если ещё не было такой истории
                        if (!passedPaths.containsKey(history)){
                            passedPaths.put(new ArrayList<>(history), new HashSet<>());
                            this.openWindowPoint = previousPoint;
                            wasInterleavings++;
                            closeWindow = checkPoint;
                            switchInterleavingThread();
                        }
                    }
                    //если точка задана и мы в ней
                    else if (previousPoint.equals(this.openWindowPoint)){
                        wasInterleavings++;
                        closeWindow = checkPoint;
                        switchInterleavingThread();
                    }
                }
                //еслибыло одно прерывание
                else if (wasInterleavings == 1) {
                    List<CheckPoint> previousHistory = history.subList(0, history.indexOf(this.openWindowPoint) + 1);
                    //если мы уже прошли как минимум 1 точку второго потока и эта пройденная точка не была рассмотрена
                    if (previousPoint.threadId == checkPoint.threadId
                            && !passedPaths.get(previousHistory).contains(history) && isNeedInterleave(openWindowPoint, closeWindow, previousPoint)) {
                        passedPaths.get(previousHistory).add(new ArrayList<>(history));
                        wasInterleavings++;
                        switchInterleavingThread();
                    }
                }
                else if (wasInterleavings == 2) {
                    logger.println("was Interleavings = " + wasInterleavings);
                }
            }
        }
    }

    private boolean isNeedInterleave(CheckPoint openWindow, CheckPoint closeWindow, CheckPoint anotherPoint) {
        if (openWindow.type == AccessType.WRITE)
            return true;
        else if (anotherPoint.type == AccessType.WRITE)
            return true;
        return false;
    }

    private void switchInterleavingThread(){
        if (currentThreadNum.get() == interleavingThreads.getKey()) {
            currentThreadNum.set(interleavingThreads.getValue());
            driver.switchThread(currentThreadNum);
        } else {
            currentThreadNum.set(interleavingThreads.getKey());
            driver.switchThread(currentThreadNum);
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
        logger.println("Iteration №" + iteration + " Invocation №" + invocation + " FirstCheckPoint" + openWindowPoint);
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
        history.clear();
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
        openWindowPoint = null;
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

    //endregion

    //region WorkFromLincheck
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
    //endregion

    private enum AccessType {
        READ,
        WRITE
    }

    /**
     * Class describing Point with respect to location and thread id`s
     */
    private class CheckPoint{
        public final int location;
        public final int threadId;
        public final AccessType type;

        public CheckPoint(int location, int threadId, AccessType type) {
            this.location = location;
            this.threadId = threadId;
            this.type = type;
        }

        @Override
        public String toString() {
            return "CheckPoint<" +
                    type +
                    location +
                    ", " + threadId + '>';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            CheckPoint that = (CheckPoint) o;


            return threadId == that.threadId && location == that.location && type == that.type;
        }

        @Override
        public int hashCode() {
            int result = location;
            result = 31 * result + threadId;
            return result;
        }
    }

    private class InterleavedPoint{
        public final CheckPoint point;
        public final List<CheckPoint> history;


        public InterleavedPoint(CheckPoint point, List<CheckPoint> history) {
            this.point = point;
            this.history = history;
        }

        @Override
        public int hashCode() {
            return super.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;

            InterleavedPoint that = (InterleavedPoint) obj;

            return (this.history == that.history && this.point == that.point);
        }

        @Override
        public String toString() {
            return "InterleavedPoint{" +
                    "point=" + point +
                    ", history=" + history +
                    '}';
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
