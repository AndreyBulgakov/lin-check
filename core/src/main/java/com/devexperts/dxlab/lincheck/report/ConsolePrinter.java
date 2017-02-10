package com.devexperts.dxlab.lincheck.report;

/**
 * Created by alexander on 09.02.17.
 */
public class ConsolePrinter extends Printer {

    public ConsolePrinter(int iterations, int invokations) {
        super(iterations, invokations);
    }

    @Override
    public void printReport() {
        System.out.println("Number iterations: " + iterations);

        System.out.println("Number invokations per iteration: " + invokations);

        for (int i = 1; i <= iterationResult.size(); i++){
            System.out.println("for iteration №" + i + " genered algorythm");
            algorythmPerIteration.get(i).forEach(System.out::println);

            System.out.println("Linearizable results:");
            linearizeResults.get(i).forEach(possibleResults -> {
                possibleResults.forEach(System.out::println);
                System.out.println();
            });

            System.out.println(iterationResult.get(i));
        }
    }
}
