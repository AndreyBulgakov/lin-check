package com.devexperts.dxlab.lincheck.report;

import com.devexperts.dxlab.lincheck.CTestConfiguration;

import java.io.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Reporter implements Closeable {
    public static final List<String> list = Collections.unmodifiableList(
            new ArrayList<String>() {{
                add("TestName");
                add("StrategyName");
                add("MaxIterations");
                add("MaxInvocations");
                add("ThreadConfig");
                add("WasIterations");
                add("WasInvocations");
                add("PassedTime");
                add("Result");
            }});
    private FileWriter filestream;
    private TestReport report;
    protected Instant time;

    public Reporter(String testName, String strategyName){
        report = new TestReport(testName, strategyName);
        File file = new File("TestResult");
        try {
            if (!file.exists()) {
                filestream = new FileWriter(file, true);
                filestream.write(list + "\n");
            } else {
                filestream = new FileWriter(file, true);
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
    * Printing test result "OK"
    * @param iteration Number of current iteration
    * @param invokation Number of current invokation
     */
    public void addCompletedResult(int iteration, int invokation){
        report.addInvokations(invokation);
        try {
            if (iteration == report.getMaxIterations()) {
                writeToFile(iteration, "Completed");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
    * Printing test result "Bad" with printing nonlinearize result
    * @param iteration Number of current iteration
    * @param invokation Number of current invokation
     */
    public void addFailedResult(int iteration, int invokation){
        report.addInvokations(invokation);
        try {
            writeToFile(iteration, "Failed");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setConfiguratuon(CTestConfiguration configuration) {
        report.setConfiguration(configuration.getIterations(), configuration.getInvocationsPerIteration(),
                configuration.getThreadConfigurations().toString());
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

    protected void writeToFile(int iteration, String result) throws IOException {
        long passedTime = Instant.now().toEpochMilli() - time.toEpochMilli();
        report.setPassedTime(passedTime);
        report.setResult(result);
        report.setWasIterations(iteration);
        filestream.write(report.toString());
        filestream.flush();
        setCurrentTime();
    }
}
