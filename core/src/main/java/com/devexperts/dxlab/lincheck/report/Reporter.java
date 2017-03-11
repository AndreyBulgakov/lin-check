package com.devexperts.dxlab.lincheck.report;
// TODO new package for one class?

import com.devexperts.dxlab.lincheck.CTestConfiguration;

import java.io.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

//TODO introduce TestReport class
public class Reporter implements Closeable {
    public static final List<String> list = Collections.unmodifiableList(
            new ArrayList<String>() {{
                add("TestName");
                add("StrategyName");
                add("MaxIteraions");
                add("MaxInvocations");
                add("ThreadConfig");
                add("WasIterations");
                add("WasInvokations");
                add("PassedTime");
                add("Result");
            }});
    private FileWriter filestream;
    private String testName;
    private String strategyName;
    private CTestConfiguration testConfig;
    private int totalInvokations;
    protected Instant time;

    public Reporter(String testName, String strategyName){
        this.testName = testName;
        this.strategyName = strategyName;
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
        addTotalInvocations(invokation);
        try {
            if (iteration == testConfig.getIterations()) {
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
        addTotalInvocations(invokation);
        try {
            writeToFile(iteration, "Failed");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setConfiguratuon(CTestConfiguration configuration) {
        testConfig = configuration;
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

    private void addTotalInvocations(int completedInv){
        totalInvokations += completedInv;
    }

    protected void writeToFile(int iteration, String result) throws IOException {
        long passedTime = Instant.now().toEpochMilli() - time.toEpochMilli();
        filestream.write(testName + "," + strategyName + "," + testConfig.getIterations() + "," + testConfig.getInvocationsPerIteration() +
                "," + testConfig.getThreadConfigurations() + ","
                + iteration + "," + totalInvokations + "," + passedTime + "," + result + "\n");
        filestream.flush();
        setCurrentTime();
    }
}
