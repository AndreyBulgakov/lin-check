package com.devexperts.dxlab.lincheck.report;

import com.devexperts.dxlab.lincheck.Actor;
import com.devexperts.dxlab.lincheck.Result;

import java.io.*;
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

    public Reporter(String testName, String strategyName, PrintStream outputstream){
        this.testName = testName;
        this.strategyName = strategyName;

        File file = new File("TestResult");
        try {
            if (!file.exists()) {
                filestream = new FileWriter(file, true);
                filestream.write("TestName, StrategyName, MaxIterations, MaxInvocations, WasIterations, WasInvokations, Result\n");
            } else {
                filestream = new FileWriter(file, true);
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }
        printer = outputstream;
    }

    public void addActors(int iteration, List<List<Actor>> actorsPerThread){
        printer.println("for iteration №" + iteration + " genered algorythm:");
        actorsPerThread.forEach(printer::println);
    }

    public void addLinearizeResults(int iteration, Set<List<List<Result>>> results){
        printer.println("Linearizable results:");
        results.forEach(possibleResults -> {
            possibleResults.forEach(printer::println);
            printer.println();
        });
    }

    public void addResult(int iteration, int invokation){
        printer.println("Iteration №" + iteration +" completed with number invokations = " + invokation);
        try {
            if (iteration == maxIter) {
                filestream.write(testName + "," + strategyName + "," + maxIter + "," + maxInv + "," + iteration + "," + invokation + "," + "Ok\n");
                filestream.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void addResult(int iteration, int invokation, List<List<Result>> nonLinearizeResults){
        StringBuilder result = new StringBuilder();
        nonLinearizeResults.forEach(res -> result.append(res.toString()));
        printer.println("For invocation" + invokation + "result was " + result);
        try {
            filestream.write(testName + "," + strategyName + "," + maxIter+ "," + maxInv+ "," + iteration + "," + invokation + "," + "Crushed\n");
            filestream.flush();
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
}
