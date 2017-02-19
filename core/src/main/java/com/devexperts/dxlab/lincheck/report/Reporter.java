package com.devexperts.dxlab.lincheck.report;

import com.devexperts.dxlab.lincheck.Actor;
import com.devexperts.dxlab.lincheck.Result;

import java.io.PrintStream;
import java.util.List;
import java.util.Set;

/**
 * Created by alexander on 09.02.17.
 */
public class Reporter {

    protected PrintStream printer;

    public Reporter(int iterations, int invokations, PrintStream outputstream){
        printer = outputstream;
        printer.println("Number iterations: " + iterations);
        printer.println("Number invocations per iteration: " + invokations + "\n");
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

    public void addResult(int iteration){
        printer.println("Iteration №" + iteration +" completed");
    }

    public void addResult(int iteration, int invokation, List<List<Result>> nonLinearizeResults){
        StringBuilder result = new StringBuilder();
        nonLinearizeResults.forEach(res -> result.append(res.toString()));
        printer.println("For invocation" + invokation + "result was " + result);
    }
}
