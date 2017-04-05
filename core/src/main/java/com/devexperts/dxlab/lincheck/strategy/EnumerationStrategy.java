package com.devexperts.dxlab.lincheck.strategy;

import com.devexperts.dxlab.lincheck.LinCheckThread;
import javafx.util.Pair;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

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
    private volatile List<Integer> executionQueue;
    private volatile boolean needInterleave = true;
    private volatile boolean needChangeFirstThread = false;
    private volatile List<CheckPoint> history = new ArrayList<>();

    @Override
    public void onSharedVariableRead(int location) {
        if (Thread.currentThread() instanceof LinCheckThread){
            LinCheckThread th = (LinCheckThread) Thread.currentThread();
            //ждем, пока можно будет продолжить выполнение
            while (th.getThreadId() != currentThread){}
            logger.println("\t\tEnter on SharedRead");
            logger.println("\t\tThread id: " + th.getThreadId() + " currentID: " + currentThread);
            logger.println("\t\tCurrentLocation id" + location);
            CheckPoint currentPoint = new CheckPoint(location, th.getThreadId());

            onSharedVariableAccess(currentPoint);
            //переключились и снова ждем, когда можно будет продолжить выполнение
            while (th.getThreadId() != currentThread){}
        }
    }

    @Override
    public void onSharedVariableWrite(int location) {
        if (Thread.currentThread() instanceof LinCheckThread){
            LinCheckThread th = (LinCheckThread) Thread.currentThread();
            while (th.getThreadId() != currentThread){}
            logger.println("\t\tEnter on SharedWrite");
            logger.println("\t\tThread id: " + th.getThreadId() + " currentID: " + currentThread);
            logger.println("\t\tCurrentLocation id" + location);
            CheckPoint currentPoint = new CheckPoint(location, th.getThreadId());
            onSharedVariableAccess(currentPoint);
            //переключились и снова ждем, когда можно будет продолжить выполнение
            while (th.getThreadId() != currentThread){}
        }
    }

    @Override
    public void endOfThread() {
        if (Thread.currentThread() instanceof LinCheckThread) {
            LinCheckThread th = (LinCheckThread) Thread.currentThread();
            logger.println("\tEndOfThread " + th.getThreadId() + "with interleavings:" + wasInterleavings);

            //В конце потока смотрим, если это поток, который не надо было прерывать - выполняем слеующий в расписании
            //иначе - выполняем некоторую логику.
            if (wasInterleavings == 0) {
                if (currentThread == interleavingThreads.getKey()) {
                    firstCheckPoint = null;
                    needInterleave = false;
                    needChangeFirstThread = true;
                }
                int index = executionQueue.indexOf(currentThread) + 1;

                if (index < executionQueue.size()) {
                    changeCurrentThreadTo(executionQueue.get(executionQueue.indexOf(currentThread) + 1));
                    //currentThread = executionQueue.get(executionQueue.indexOf(currentThread) + 1);
                }
            }
            else if (wasInterleavings == 1) {
                firstCheckPoint = null;
                needInterleave = false;
                if (currentThread == interleavingThreads.getValue()) {
                    changeCurrentThreadTo(interleavingThreads.getKey());
                    //currentThread = interleavingThreads.getKey();
                }
                else if (currentThread == interleavingThreads.getKey()) {
                    int index = executionQueue.indexOf(currentThread) + 1;
                    if (index < executionQueue.size() - 1)
                        changeCurrentThreadTo(executionQueue.get(executionQueue.indexOf(currentThread) + 2));
                        //currentThread = executionQueue.get(executionQueue.indexOf(currentThread) + 2);
                }
                else {

                    int index = executionQueue.indexOf(currentThread) + 1;

                    if (index < executionQueue.size())
                        changeCurrentThreadTo(executionQueue.get(executionQueue.indexOf(currentThread) + 1));
                        //currentThread = executionQueue.get(executionQueue.indexOf(currentThread) + 1);
                }
            }
            else if (wasInterleavings == 2) {

                int index = executionQueue.indexOf(currentThread) + 1;

                if (index < executionQueue.size())
                    changeCurrentThreadTo(executionQueue.get(executionQueue.indexOf(currentThread) + 1));
                    //currentThread = executionQueue.get(executionQueue.indexOf(currentThread) + 1);
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
    private void onSharedVariableAccess(CheckPoint currentPoint) {
        if (currentThread == interleavingThreads.getKey() || currentThread == interleavingThreads.getValue()) {
            if (needInterleave) {
                //если нам нужно найти новую точку и находим - переключаемся
                if (wasInterleavings == 0 && firstCheckPoint == null && !passedPaths.containsKey(currentPoint)) {
                    passedPaths.put(currentPoint, new HashSet<>());
                    firstCheckPoint = currentPoint;
                    wasInterleavings++;
                    if (currentThread == interleavingThreads.getKey())
                        changeCurrentThreadTo(interleavingThreads.getValue());
                    else
                        changeCurrentThreadTo(interleavingThreads.getKey());
                }
                //если нужно найти новую точку и не находим - пропускаем
                else if (wasInterleavings == 0 && firstCheckPoint == null && passedPaths.containsKey(currentPoint)) {
                }
                //если не нужно искать новую точку и мы на ней - переключаемся
                else if (wasInterleavings == 0 && firstCheckPoint != null && currentPoint.equals(firstCheckPoint)) {
                    wasInterleavings++;
                    if (currentThread == interleavingThreads.getKey())
                        changeCurrentThreadTo(interleavingThreads.getValue());
                    else
                        changeCurrentThreadTo(interleavingThreads.getKey());
                }
                //если не нужно искать новую точку, но мы не на ней - не переключаемся
                else if (wasInterleavings == 0 && firstCheckPoint != null && !currentPoint.equals(firstCheckPoint)) {
                } else if (wasInterleavings == 1 && !passedPaths.get(firstCheckPoint).contains(currentPoint)) {
                    passedPaths.get(firstCheckPoint).add(currentPoint);
                    wasInterleavings++;
                    if (currentThread == interleavingThreads.getKey())
                        changeCurrentThreadTo(interleavingThreads.getValue());
                    else
                        changeCurrentThreadTo(interleavingThreads.getKey());
                }
            }
        }
    }

    /**
     * Method for printing information about current invocation
     * @param iteration - current iteration
     * @param invocation - current invocation
     */
    public void printHeader(int iteration, int invocation) {
        logger.println("Iteration №" + iteration + " Invocation №" + invocation + " FirstCheckPoint" + firstCheckPoint);
    }

    /**
     * Clear information about previous running
     * @param firstThread
     * @param needChangeFirstThread
     */
    public void prepareInvocation(int firstThread, boolean needChangeFirstThread){
        needInterleave = true;
        currentThread = executionQueue.get(0);
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
        executionQueue = queue;
        currentThread = executionQueue.get(0);
        this.interleavingThreads = interleavedThreads;
        passedPaths = new HashMap<>();
        logger.println("Current queue of threads execution" + queue);
        logger.println("Current interleaving threads id's" + interleavedThreads);
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


    /**
     * Содержит логику для смены потока
     * @param n - номер нового потока
     */
    private void changeCurrentThreadTo(int n) {
        currentThread = n;
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
}
