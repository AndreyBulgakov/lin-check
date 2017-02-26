package com.devexperts.dxlab.lincheck.report;

import com.devexperts.dxlab.lincheck.Actor;
import com.devexperts.dxlab.lincheck.Result;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.time.Instant;
import java.util.List;
import java.util.Set;

/**
 * Created by alexander on 09.02.17.
 */
public class Reporter {

    protected PrintStream printer;
    protected FileWriter filestream;
    protected String testName;
    protected String strategyName;
    protected int maxIter;
    protected int maxInv;
    protected int totalInvokations;
    protected Instant time;

    public Reporter(String testName, String strategyName, PrintStream outputstream){
        this.testName = testName;
        this.strategyName = strategyName;
        File file = new File("TestResult");
        try {
            if (!file.exists()) {
                filestream = new FileWriter(file, true);
                filestream.write("TestName, StrategyName, MaxIterations, MaxInvocations, WasIterations, WasInvokations, PassedTime, Result\n");
            } else {
                filestream = new FileWriter(file, true);
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }
        printer = outputstream;
        time = Instant.now();
    }

    public void addActors(int iteration, List<List<Actor>> actorsPerThread){
        Instant printTime = Instant.now();
        printer.println("for iteration №" + iteration + " genered algorythm:");
        actorsPerThread.forEach(printer::println);
        time = Instant.ofEpochMilli(Instant.now().toEpochMilli() - printTime.toEpochMilli() + time.toEpochMilli());
    }

    public void addLinearizeResults(int iteration, Set<List<List<Result>>> results){
        Instant printTime = Instant.now();
        printer.println("Linearizable results:");
        results.forEach(possibleResults -> {
            possibleResults.forEach(printer::println);
            printer.println();
        });
        time = Instant.ofEpochMilli(Instant.now().toEpochMilli() - printTime.toEpochMilli() + time.toEpochMilli());
    }

    public void addResult(int iteration, int invokation){
        Instant printTime = Instant.now();
        printer.println("Iteration №" + iteration +" completed with number invokations = " + invokation);
        addTotalInvocations(invokation);
        time = Instant.ofEpochMilli(Instant.now().toEpochMilli() - printTime.toEpochMilli() + time.toEpochMilli());
        try {
            if (iteration == maxIter) {
                writeToFile(iteration, "Completed");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addResult(int iteration, int invokation, List<List<Result>> nonLinearizeResults){
        Instant printTime = Instant.now();
        StringBuilder result = new StringBuilder();
        nonLinearizeResults.forEach(res -> result.append(res.toString()));
        printer.println("For invocation" + invokation + "result was " + result);
        addTotalInvocations(invokation);
        time = Instant.ofEpochMilli(Instant.now().toEpochMilli() - printTime.toEpochMilli() + time.toEpochMilli());
        try {
            writeToFile(iteration, "Failed");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setTestName(String name){
        testName = name;
    }

    public void setStrategyName(String strategyName) {
        this.strategyName = strategyName;
    }

    public void setMaxIterAndInv(int iterations, int invokation){
        this.maxIter = iterations;
        this.maxInv = invokation;
        printer.println("Number iterations: " + maxIter);
        printer.println("Number invocations per iteration: " + maxInv + "\n");
    }

    public void close(){
        try {
            filestream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setCurrentTime(){
        time = Instant.now();
    }

    protected void addTotalInvocations(int completedInv){
        totalInvokations += completedInv;
    }

    protected void writeToFile(int iteration, String result) throws IOException {
        long passedTime = Instant.now().toEpochMilli() - time.toEpochMilli();
        filestream.write(testName + "," + strategyName + "," + maxIter+ "," + maxInv+ "," + iteration +
                "," + totalInvokations + "," + passedTime + "," + result + "\n");
        filestream.flush();
        setCurrentTime();
    }
}
