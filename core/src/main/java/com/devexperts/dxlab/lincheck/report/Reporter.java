package com.devexperts.dxlab.lincheck.report;
// TODO new package for one class?

import com.devexperts.dxlab.lincheck.Actor;
import com.devexperts.dxlab.lincheck.CTestConfiguration;
import com.devexperts.dxlab.lincheck.Result;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.time.Instant;
import java.util.List;
import java.util.Set;

/**
 * // TODO remove such comments
 * TODO documentation
 * TODO make it Closable
 * TODO introduce TestReport class
 * Created by alexander on 09.02.17.
 */
public class Reporter {
    // TODO why protected?
    // TODO what these parameters are doing here?
    protected PrintStream printer; // TODO why are you insert "logging" in report class?
    protected FileWriter filestream;
    protected String testName;
    protected String strategyName;
    protected int maxIter;
    protected int maxInv;
    protected int totalInvokations;
    protected Instant time;
    protected String threadsConfigString;

    public Reporter(String testName, String strategyName, PrintStream outputstream){
        this.testName = testName;
        this.strategyName = strategyName;
        File file = new File("TestResult");
        try {
            if (!file.exists()) {
                filestream = new FileWriter(file, true);
                // TODO Do not inline fields in code, use list/array of constants instead
                filestream.write("TestName, StrategyName, MaxIterations, MaxInvocations, ThreadConfig, WasIterations, WasInvokations, PassedTime, Result\n");
            } else {
                filestream = new FileWriter(file, true);
            }
        } catch (IOException e){
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

    // TODO What about storing CTestConfiguration?
    public void setConfiguratuon(CTestConfiguration configuration) {
        this.maxIter = configuration.getIterations();
        this.maxInv = configuration.getInvocationsPerIteration();
        printer.println("Number iterations: " + maxIter); // TODO why should we print it?
        printer.println("Number invocations per iteration: " + maxInv + "\n");
        this.threadsConfigString = configuration.getThreadConfigurations().toString();
    }

    public void close(){
        try {
            filestream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // TODO should this method exists?
    public void setCurrentTime(){
        time = Instant.now();
    }

    // TODO why these methods are protected?
    protected void addTotalInvocations(int completedInv){
        totalInvokations += completedInv;
    }

    protected void writeToFile(int iteration, String result) throws IOException {
        long passedTime = Instant.now().toEpochMilli() - time.toEpochMilli();
        filestream.write(testName + "," + strategyName + "," + maxIter+ "," + maxInv+ "," + threadsConfigString + ","
                + iteration + "," + totalInvokations + "," + passedTime + "," + result + "\n");
        filestream.flush();
        setCurrentTime();
    }
}
