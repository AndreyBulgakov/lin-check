package com.devexperts.dxlab.lincheck;

import com.devexperts.dxlab.lincheck.util.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;

public class Checker {
    Caller caller;
    int COUNT_ITER;
    int COUNT_THREADS;

    public Checker() {
    }

    private void genPermutationsHelper(List<Actor[]> result, Actor[] out, int countUsed, int countActors, Actor[][] actors, int[] inds) {
        if (countActors == countUsed) {
            result.add(out.clone());
        } else {
            for (int i = 0; i < actors.length; i++) {
                if (actors[i].length != inds[i]) {
                    Actor call = actors[i][inds[i]];
                    inds[i]++;
                    out[countUsed] = call;
                    countUsed++;

                    genPermutationsHelper(result, out, countUsed, countActors, actors, inds);

                    countUsed--;
                    inds[i]--;
                }
            }
        }
    }

    private Actor[][] genPermutations(Actor[][] actors, int countActors) {
        Actor[] out = new Actor[countActors];
        int[] inds = new int[COUNT_THREADS];
        List<Actor[]> perms = new ArrayList<>();
        genPermutationsHelper(perms, out, 0, countActors, actors, inds);

        return perms.toArray(new Actor[perms.size()][]);
    }

    private void executeActors(Caller caller, Actor[] actors, Result[] result) {
        for (Actor actor : actors) {
            caller.call(actor, result[actor.ind]);
        }
    }

    private Result[] executeActors(Caller caller, Actor[] actors) {
        Result[] result = new Result[actors.length];
        for (int i = 0; i < actors.length; i++) {
            result[i] = new Result();
        }
        executeActors(caller, actors, result);
        return result;
    }

    private Result[][] executeLinear(Actor[][] actors, int countActors) {
        Actor[][] perms = genPermutations(actors, countActors);

        // print all possible executions
        for (Actor[] perm : perms) {
            System.out.println(Arrays.asList(perm));
        }
        System.out.println();

        Result[][] results = new Result[perms.length][];
        for (int i = 0; i < perms.length; i++) {
            caller.reload();
            results[i] = executeActors(caller, perms[i]);
        }

        List<Result[]> uniqueResults = new ArrayList<>();

        for (Result[] result : results) {
            boolean exists = false;
            for (Result[] uniqueResult : uniqueResults) {
                if (Arrays.equals(result, uniqueResult)) {
                    exists = true;
                    break;
                }
            }
            if (!exists) {
                uniqueResults.add(result);
            }
        }

        return uniqueResults.toArray(new Result[uniqueResults.size()][]);
    }

    public boolean check(Caller caller) {
        this.caller = caller;

        for (CheckerConfiguration conf : caller.getConfigurations()) {
            if (!checkImpl(conf)) {
                return false;
            }
        }

        return true;
    }

    private boolean checkImpl(CheckerConfiguration conf) {
        COUNT_ITER = conf.getNumIterations();
        COUNT_THREADS = conf.getNumThreads();

        ExecutorService pool = Executors.newFixedThreadPool(COUNT_THREADS);
        final Phaser phaser = new Phaser(COUNT_THREADS + 1);

        boolean errorFound = false;
        for (int iter = 0; iter < COUNT_ITER; iter++) {
            System.out.println("iter = " + iter);

            Actor[][] actors = conf.generateActors();

            int countActors = 0;
            for (Actor[] actor : actors) {
                countActors += actor.length;
            }

            System.out.println("Thread configuration:");
            for (Actor[] actor : actors) {
                System.out.println(Arrays.asList(actor));
            }
            System.out.println();

            Result[][] linearResults = executeLinear(actors, countActors);
            // print linear results
            System.out.println();
            for (Result[] linearResult : linearResults) {
                System.out.println(Arrays.asList(linearResult));
            }
            System.out.println();

            int[] cntLinear = new int[linearResults.length];

            System.out.println("Progress:");
            System.out.printf("[%d] ", 100000);

            final Result[] results = new Result[countActors];
            for (int i = 0; i < countActors; i++) {
                results[i] = new Result();
            }

            Runnable[] runnables = new Runnable[COUNT_THREADS];
            for (int i = 0; i < COUNT_THREADS; i++) {
                final Actor[] threadActors = actors[i];
                runnables[i] = new Runnable() {
                    @Override
                    public void run() {
                        phaser.arriveAndAwaitAdvance();

                        long r = MyRandom.nextLong() % 10000L;
                        busyWait(r);

                        executeActors(caller, threadActors, results);
                        phaser.arrive();
                    }
                };
            }

            for (int threads_num = 0; threads_num < 100000; threads_num++) {
                if (threads_num % 10000 == 0) {
                    System.out.printf("%d ", threads_num);
                }

                caller.reload();
                for (int i = 0; i < COUNT_THREADS; i++) {
                    pool.execute(runnables[i]);
                }

                phaser.arriveAndAwaitAdvance();
                phaser.arriveAndAwaitAdvance();



                boolean correct = false;
                for (int i = 0; i < linearResults.length; i++) {
                    if (Arrays.equals(linearResults[i], results)) {
                        correct = true;
                        cntLinear[i]++;
                        break;
                    }
                }
                if (!correct) {
                    System.out.println();
                    System.out.println("Error. Unexpected result:");
                    System.out.println(Arrays.asList(results));
                    errorFound = true;
                    break;
                }
            }
            System.out.println();
            System.out.printf("Histogram: ");
            for (int cnt : cntLinear) {
                System.out.printf("%d ", cnt);
            }
            System.out.println();
            System.out.println();
            if (errorFound) {
                break;
            }
        }
        pool.shutdown();
        System.out.println("finish");
        return !errorFound;
    }

    private static void busyWait(long nanos) {
        for (long start = System.nanoTime(); start + nanos >= System.nanoTime(); ){}
    }
}
