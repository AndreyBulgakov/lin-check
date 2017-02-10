package com.devexperts.dxlab.lincheck.report;

import com.devexperts.dxlab.lincheck.Actor;
import com.devexperts.dxlab.lincheck.Result;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by alexander on 09.02.17.
 */
public abstract class Printer {

    protected Map<Integer, List<List<Actor>>> algorythmPerIteration = new HashMap<>();

    protected Map<Integer, Set<List<List<Result>>>> linearizeResults = new HashMap<>();

    protected Map<Integer, String> iterationResult = new HashMap<>();

    protected int iterations;

    protected int invokations;

    public Printer(int iterations, int invokations){
        this.iterations = iterations;
        this.invokations = invokations;
    }

    public abstract void printReport();

    public void addAlgoryrhm(int iteration, List<List<Actor>> algorythm){
        algorythmPerIteration.put(iteration, algorythm);
    }

    public void addLinearizeResults(int iteration, Set<List<List<Result>>> results){
        linearizeResults.put(iteration, results);
    }

    public void addResult(int iteration){
        iterationResult.put(iteration, "Iteration completed");
    }

    public void addResult(int iteration, int invokation, List<List<Result>> nonLinearizeResults){
        StringBuilder result = new StringBuilder();
        nonLinearizeResults.forEach(res -> result.append(res.toString()));
        iterationResult.put(iteration, "For invocation" + invokation + "result was " + result);
    }
}
