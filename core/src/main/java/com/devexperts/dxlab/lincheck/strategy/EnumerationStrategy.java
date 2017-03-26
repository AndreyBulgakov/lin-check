package com.devexperts.dxlab.lincheck.strategy;

import com.devexperts.dxlab.lincheck.LinCheckThread;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static java.nio.file.StandardOpenOption.APPEND;

public class EnumerationStrategy implements Strategy {
    private Map<CheckPoint, Set<CheckPoint>> passedPaths = new HashMap<>();
    private CheckPoint firstCheckPoint;
    PrintStream logger = getLogger();
    private volatile int currentThread = 1;
    private volatile int wasInterleavings = 0;
    private volatile boolean needInterleave = true;
    private volatile boolean needChangeFirstThread = false;

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

    public void printHeader(int iter, int invocation) {
        logger.println("Iteration №" + iter + " Invocation №" + invocation + " FirstCheckPoint" + firstCheckPoint);
    }

    public void returnDatas(int firstThread, boolean needChangeFirstThread){
        needInterleave = true;
        currentThread = firstThread;
        this.needChangeFirstThread = needChangeFirstThread;
        wasInterleavings = 0;
    }

    public boolean isNeedChangeFirstThread() {
        return needChangeFirstThread;
    }

    public void startNewIteration() {
        printTraces();
        firstCheckPoint = null;
        passedPaths = new HashMap<>();
        returnDatas(1, false);
    }

    @Override
    public void endOfThread() {
        if (Thread.currentThread() instanceof LinCheckThread) {
            LinCheckThread th = (LinCheckThread) Thread.currentThread();
            logger.println("\tEndOfThread " + th.getThreadId() + "with interleavings:" + wasInterleavings);
            //если было 1 прерывание => просмотрели всё, нужно менять первую точку
            if (wasInterleavings == 1) {
                firstCheckPoint = null;
                needInterleave = false;
            }
            //если было 0 прерываний => закончили смотреть первые переключения, нужно исследовать другие
            if (wasInterleavings == 0) {
                firstCheckPoint = null;
                needInterleave = false;
                needChangeFirstThread = true;
            }
            //меняем поток
            currentThread = currentThread == 1 ? 2 : 1;

        }
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

    public void printTraces() {
        passedPaths.forEach((a,b) -> logger.println(a + " with set: " + b));
    }

    private void onSharedVariableAccess(CheckPoint currentPoint) {
        //TODO переключение потока - одтельный метод со своей логикой
        if (needInterleave) {
            //если нам нужно найти новую точку и находим - переключаемся
            if (wasInterleavings == 0 && firstCheckPoint == null && !passedPaths.containsKey(currentPoint)) {
                passedPaths.put(currentPoint, new HashSet<>());
                firstCheckPoint = currentPoint;
                wasInterleavings++;
                currentThread = 2 - currentThread + 1;
            }
            //если нужно найти новую точку и не находим - пропускаем
            else if (wasInterleavings == 0 && firstCheckPoint == null && passedPaths.containsKey(currentPoint)) {
            }
            //если не нужно искать новую точку и мы на ней - переключаемся
            else if (wasInterleavings == 0 && firstCheckPoint != null && currentPoint.equals(firstCheckPoint)) {
                wasInterleavings++;
                currentThread = 2 - currentThread + 1;
            }
            //если не нужно искать новую точку, но мы не на ней - не переключаемся
            else if (wasInterleavings == 0 && firstCheckPoint != null && !currentPoint.equals(firstCheckPoint)) {
            }
            else if (wasInterleavings == 1 && !passedPaths.get(firstCheckPoint).contains(currentPoint)) {
                passedPaths.get(firstCheckPoint).add(currentPoint);
                wasInterleavings++;
                currentThread = 2 - currentThread + 1;
            }
        }
    }

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

    private enum PointType{
        READ, WRITE
    }
}
