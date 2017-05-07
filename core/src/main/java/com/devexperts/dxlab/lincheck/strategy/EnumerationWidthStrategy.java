package com.devexperts.dxlab.lincheck.strategy;

import co.paralleluniverse.fibers.Suspendable;
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

public class EnumerationWidthStrategy implements Strategy {
    private Map<ArrayList<CheckPoint>, Set<ArrayList<CheckPoint>>> passedPaths;
    private ArrayList<CheckPoint> openWindowPoints;
    private CheckPoint openWindowPoint;
    PrintStream logger = getLogger();
    private AtomicInteger currentThreadNum;
    private volatile int wasInterleavings;
    private volatile boolean needInterleave;
    private volatile boolean isNeedChangeParameters;
    private volatile List<CheckPoint> history;
    private final String strandName = "LinCheckStrand";
    private EnumerationStrategyHelper helper;
    private Driver driver;
    private boolean isNeedStopIteration;


    private Map<ScheduleParameter, Set<List<CheckPoint>>> checkedPoints;
    private Map<ScheduleParameter, Set<List<CheckPoint>>> nonCheckedOpenWindows;

    public EnumerationWidthStrategy(Driver driver) {
        this.driver = driver;
        wasInterleavings = 0;
        history = new ArrayList<>();
        needInterleave = true;
        isNeedChangeParameters = false;
        passedPaths = new HashMap<>();
        checkedPoints = new HashMap<>();
        nonCheckedOpenWindows = new HashMap<>();
        isNeedStopIteration = false;
    }


    @Override
    public void startOfThread() {
        if (driver.getCurrentThreadName().equals(strandName)) {
            driver.waitFor(currentThreadNum);
        }
    }

    @Override
    public void onSharedVariableRead(int location) {
        if (driver.getCurrentThreadName().equals(strandName)) {
            int th = driver.getCurrentThreadId();
            //если история не пуста, то, возможно, стоит переключиться
            CheckPoint currentPoint = new CheckPoint(location, th, AccessType.READ);
            logger.println("\n\t\tEnter on " + currentPoint + " currentID: " + currentThreadNum.get());
            if (wasInterleavings < 2 && history.size() > 0){
                CheckPoint prevoiusPoint = history.get(history.size() - 1);
                onSharedVariableAccess(currentPoint, prevoiusPoint);
            }
            history.add(currentPoint);
            logger.println("\tEnd of " + currentPoint);
        }
    }

    @Override
    public void onSharedVariableWrite(int location) {
        if (driver.getCurrentThreadName().equals(strandName)) {
            int th = driver.getCurrentThreadId();
            CheckPoint currentPoint = new CheckPoint(location, th, AccessType.WRITE);
            logger.println("\n\t\tEnter on " + currentPoint + " currentID: " + currentThreadNum.get());
            if (wasInterleavings < 2 && history.size() > 0){
                CheckPoint prevoiusPoint = history.get(history.size() - 1);
                onSharedVariableAccess(currentPoint, prevoiusPoint);
            }
            history.add(currentPoint);
            logger.println("\tEnd of " + currentPoint);
        }
    }

    @Suspendable
    private void onSharedVariableAccess(CheckPoint checkPoint, CheckPoint previousPoint) {
        if (currentThreadNum.get() == helper.interleavingThreads.getKey() || currentThreadNum.get() == helper.interleavingThreads.getValue()) {
            if (needInterleave) {
                //если ещё не было прерываний
                if (wasInterleavings == 0){
                    //если точка не задана
                    if (!checkedPoints.get(helper.queueExecutions.get(helper.executionIndex)).contains(history) && (openWindowPoints == null || history.size() > openWindowPoints.size())){
                        //если ещё не было такой истории то добавляем

                        if (!passedPaths.containsKey(history)) {
                            passedPaths.put(new ArrayList<>(history), new HashSet<>());

                        }
                        //добавляем новую историю в список неисследованных

                        if (nonCheckedOpenWindows.get(helper.queueExecutions.get(helper.executionIndex)).add(new ArrayList<>(history))){
        //                    System.out.println(history);
                        }

                        openWindowPoints = new ArrayList<>(history);
                        history.clear();
                        this.openWindowPoint = previousPoint;
                        wasInterleavings++;
                        switchInterleavingThread();
                    }
                }
                //еслибыло одно прерывание
                else if (wasInterleavings == 1) {
                    //если мы уже прошли как минимум 1 точку второго потока и эта пройденная точка не была рассмотрена
                    if (previousPoint.threadId == checkPoint.threadId
                            && !passedPaths.get(openWindowPoints).contains(history)
                            && isNeedInterleave(openWindowPoint, previousPoint)) {
                        passedPaths.get(openWindowPoints).add(new ArrayList<>(history));
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

    private void switchInterleavingThread(){
        if (currentThreadNum.get() == helper.interleavingThreads.getKey()) {
            currentThreadNum.set(helper.interleavingThreads.getValue());
            driver.switchThread(currentThreadNum);
        } else {
            currentThreadNum.set(helper.interleavingThreads.getKey());
            driver.switchThread(currentThreadNum);
        }
    }

    private boolean isNeedInterleave(CheckPoint openWindow, CheckPoint anotherThreadPoint) {
        if (openWindow.type == AccessType.WRITE)
            return true;
        else if (anotherThreadPoint.type == AccessType.WRITE)
            return true;
        return false;
    }

    @Override
    public void endOfThread() {
        if (driver.getCurrentThreadName().equals(strandName)) {
            int th = driver.getCurrentThreadId();
            logger.println("\tEndOfThread " + th + "with interleavings:" + wasInterleavings);
            //В конце потока смотрим, если это поток, который не надо было прерывать - выполняем слеующий в расписании
            //иначе - выполняем некоторую логику.
            int currentThreadIndex = helper.threadQueue.indexOf(currentThreadNum.get()) + 1;
            if (wasInterleavings == 0) {
                if (currentThreadNum.get() == helper.interleavingThreads.getKey()) {
                    helper.queueExecutions.get(helper.executionIndex).setLastWindow(history);
                    needInterleave = false;
                    isNeedChangeParameters = true;
                }

                if (currentThreadIndex < helper.threadQueue.size()) {
                    currentThreadNum.set(helper.threadQueue.get(helper.threadQueue.indexOf(currentThreadNum.get()) + 1));
                    driver.switchOnEndOfThread(currentThreadNum);
                }
            }
            else if (wasInterleavings == 1) {
                needInterleave = false;

                if (currentThreadNum.get() == helper.interleavingThreads.getValue()) {
                    //добавляем точку в список исследованных
                    checkedPoints.get(helper.queueExecutions.get(helper.executionIndex)).add(openWindowPoints);
                    //удаляем исследованную историю из списка неисследованных
                    nonCheckedOpenWindows.get(helper.queueExecutions.get(helper.executionIndex)).remove(openWindowPoints);
                    //если это было последнее окно - нужно сменить окно
                    ArrayList<CheckPoint> lastwindow = helper.getCurrentParameter().lastWindow;
                    if (openWindowPoints.equals(lastwindow)) {
                        do {
                            lastwindow.remove(lastwindow.size() - 1);
                        } while (checkedPoints.get(helper.getCurrentParameter()).contains(lastwindow));
                    }

                    currentThreadNum.set(helper.interleavingThreads.getKey());
                    driver.switchOnEndOfThread(currentThreadNum);
                }
                else if (currentThreadNum.get() == helper.interleavingThreads.getKey()) {
                    if (currentThreadIndex < helper.threadQueue.size() - 1) {
                        currentThreadNum.set(helper.threadQueue.get(helper.threadQueue.indexOf(currentThreadNum.get()) + 2));
                        driver.switchOnEndOfThread(currentThreadNum);
                    }
                }
                else {
                    if (currentThreadIndex < helper.threadQueue.size()) {
                        currentThreadNum.set(helper.threadQueue.get(helper.threadQueue.indexOf(currentThreadNum.get()) + 1));
                        driver.switchOnEndOfThread(currentThreadNum);
                    }
                }
            }
            else if (wasInterleavings == 2) {
                if (currentThreadIndex < helper.threadQueue.size()) {
                    currentThreadNum.set(helper.threadQueue.get(helper.threadQueue.indexOf(currentThreadNum.get()) + 1));
                    try {
                        driver.switchOnEndOfThread(currentThreadNum);
                    }
                    catch (Throwable e) {
                        System.out.println(e);
                    }
                }
            }
            else {
                if (openWindowPoints.equals(helper.getCurrentParameter().lastWindow)){
                    isNeedChangeParameters = true;
                }
                logger.println("something else");
            }
        }
    }

    //region Logging
    /**
     * Method for printing information about current invocation
     * @param iteration - current iteration
     * @param invocation - current invocation
     */
    public void printHeader(int iteration, int invocation) {
        logger.println("Iteration №" + iteration + " Invocation №" + invocation + " FirstCheckPoint" + openWindowPoints);
        System.out.println("Iteration №" + iteration + " Invocation №" + invocation + " FirstCheckPoint" + openWindowPoints);
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

    //endregion


    @Override
    public void beforeStartIteration(int threadNumber) {
        helper = new EnumerationStrategyHelper(threadNumber);
    }

    @Override
    public void onStartIteration() {
        helper.prepareDatas(helper.threadNumber);
        helper.setCurrentParameters();
        isNeedStopIteration = false;
    }

    @Override
    public void onStartInvocation(int iteration, int invocation) {
        if (isNeedChangeParameters){
            openWindowPoints = null;
            openWindowPoint = null;
            helper.nextIndex();
            helper.setCurrentParameters();
        }
        printHeader(iteration, invocation);
        logger.println("Current queue of threads execution" + helper.threadQueue);
        logger.println("Current interleaving threads id's" + helper.interleavingThreads);
        //logger.println("CheckedPaths: " + checkedPoints);
        currentThreadNum = new AtomicInteger(helper.threadQueue.get(0));
        needInterleave = true;
        isNeedChangeParameters = false;
        wasInterleavings = 0;
        history = new ArrayList<>();
        if (!nonCheckedOpenWindows.containsKey(helper.getCurrentParameter())) {
            nonCheckedOpenWindows.put(helper.getCurrentParameter(), new HashSet<>());
            checkedPoints.put(helper.getCurrentParameter(), new HashSet<>());
        }
    }

    @Override
    public void onEndInvocation() {

        //проверяем, если карта точек пуста, то нужно удалить данный параматр из рассматриваемых
        if (isNeedChangeParameters && nonCheckedOpenWindows.get(helper.getCurrentParameter()).size() == 0) {
            helper.removeParameterFromList();
            openWindowPoints = null;
            openWindowPoint = null;
            if (helper.queueExecutions.size() > 0) {
                if (helper.executionIndex >= helper.queueExecutions.size()){
                    helper.nextIndex();
                }
                helper.setCurrentParameters();
                currentThreadNum = new AtomicInteger(helper.threadQueue.get(0));
            }
            else
                isNeedStopIteration = true;
            needInterleave = true;
            isNeedChangeParameters = false;
            wasInterleavings = 0;
            history = new ArrayList<>();
        }
    }

    @Override
    public void onEndIteration() {
        passedPaths = new HashMap<>();
        openWindowPoint = null;
        openWindowPoints = null;
        wasInterleavings = 0;
        history = new ArrayList<>();
        needInterleave = true;
        isNeedChangeParameters = false;
        passedPaths = new HashMap<>();
        checkedPoints = new HashMap<>();
        nonCheckedOpenWindows = new HashMap<>();
        isNeedStopIteration = false;

    }

    @Override
    public boolean isNeedStopIteration() {
        return isNeedStopIteration;
    }

    private class EnumerationStrategyHelper {
        //переменные для новой сратегии
        //все возможные варианты исполнений
        ArrayList<ScheduleParameter> queueExecutions;
        //индекс нынешнего исполнения в queueExecutions
        int executionIndex;
        int threadNumber;

        List<Integer> threadQueue;
        Pair<Integer, Integer> interleavingThreads;

        public EnumerationStrategyHelper(int threadNumber) {
            this.threadNumber = threadNumber;
            prepareDatas(threadNumber);
        }

        public void prepareDatas(int threadNumber){
            List<Integer> list = IntStream.range(0, threadNumber).boxed().collect(Collectors.toList());
            List<List<Integer>> queueThreadExecutions = threadPermutations(list);
            queueExecutions = new ArrayList<>();
            for (List<Integer> queue : queueThreadExecutions){
                for (int i = 0; i < queue.size() - 1; i++){
                    queueExecutions.add(new ScheduleParameter(queue, new Pair<>(queue.get(i), queue.get(i + 1))));
                }
            }
            //System.err.println(queueExecutions.get(5));
            executionIndex = 0;
        }

        public List<List<Integer>> threadPermutations(List<Integer> list) {
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

        public void setCurrentParameters(){
            ScheduleParameter parameter = queueExecutions.get(executionIndex);
            threadQueue = parameter.executionQueue;
            interleavingThreads = parameter.interleavingThreads;
        }

        public ScheduleParameter getCurrentParameter(){
            return queueExecutions.get(executionIndex);
        }

        public void nextIndex() {
            executionIndex = executionIndex >= queueExecutions.size() - 1 ? 0 : executionIndex + 1;
        }

        public void removeParameterFromList() {
            queueExecutions.remove(executionIndex);
        }
    }

    private class ScheduleParameter {
        List<Integer> executionQueue;
        Pair<Integer, Integer> interleavingThreads;
        ArrayList<CheckPoint> lastWindow;

        public ScheduleParameter(List<Integer> queue, Pair<Integer, Integer> interleavingThreads) {
            this.executionQueue = queue;
            this.interleavingThreads = interleavingThreads;
        }

        public void setLastWindow(List<CheckPoint> history) {
            lastWindow = new ArrayList<>(history);
        }

        @Override
        public String toString() {
            return "\nScheduleParameter{" +
                    "executionQueue=" + executionQueue +
                    ", interleavingThreads=" + interleavingThreads +
                    '}';
        }
    }


    private enum AccessType {
        READ,
        WRITE
    }

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
            return "<" +
                    "loc = " + location +
                    ", type = " + type +
                    ", tid = " + threadId + '>';
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
}
