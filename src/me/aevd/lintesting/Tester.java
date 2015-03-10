package me.aevd.lintesting;

import me.aevd.lintesting.util.Actor;
import me.aevd.lintesting.util.Caller;
import me.aevd.lintesting.util.Result;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

public class Tester {
    Caller caller;
    int COUNT_ITER = 1000;
    int COUNT_THREADS = 2;

    public Tester(Caller caller) {
        this.caller = caller;
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

    private Result[][] executeLinear(Actor[][] actors, int countActors) {
        Actor[][] perms = genPermutations(actors, countActors);
        // print all possible executions
        for (int i = 0; i < perms.length; i++) {
            System.out.println(Arrays.asList(perms[i]));
        }
        System.out.println();

        Result[][] results = new Result[perms.length][];

        for (int i = 0; i < perms.length; i++) {
            Actor[] localActors = perms[i];
            Result[] localResult = new Result[localActors.length];

            caller.reload();

            for (int j = 0; j < localActors.length; j++) {
                localResult[localActors[j].ind] = caller.call(localActors[j].method, localActors[j].args);
            }

            results[i] = localResult;
        }
        return results;
    }

    public void test() {
        final Random random = new Random();

        ExecutorService pool = Executors.newFixedThreadPool(COUNT_THREADS);
        final CyclicBarrier barrier = new CyclicBarrier(COUNT_THREADS);

        for (int iter = 0; iter < COUNT_ITER; iter++) {
            System.out.println("iter = " + iter);
            Thread[] threads = new Thread[COUNT_THREADS];

            Actor[][] actors = caller.generateActors(COUNT_THREADS);
            int countActors = 0;
            for (Actor[] actor : actors) {
                countActors += actor.length;
            }

            System.out.println("Thread configuration:");
            for (int i = 0; i < actors.length; i++) {
                System.out.println(Arrays.asList(actors[i]));
            }
            System.out.println();

            Result[][] linearResults = executeLinear(actors, countActors);
            // print linear results
            System.out.println();
            for (int i = 0; i < linearResults.length; i++) {
                System.out.println(Arrays.asList(linearResults[i]));
            }
            System.out.println();

            int[] cntLinear = new int[linearResults.length];

            boolean errorFound = false;
            System.out.println("Progress:");
            System.out.printf("[%d] ", 100000);
            for (int threads_num = 0; threads_num < 100000; threads_num++) {
                if (threads_num % 10000 == 0) {
                    System.out.printf("%d ", threads_num);
                }
                final Result[] results = new Result[countActors];
                caller.reload();
                List<Future<?>> futures = new ArrayList<>();
                for (int i = 0; i < COUNT_THREADS; i++) {
                    final Actor[] threadActors = actors[i];
                    Runnable r = new Runnable() {
                        @Override
                        public void run() {
                            try {
                                barrier.await();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            } catch (BrokenBarrierException e) {
                                e.printStackTrace();
                            }
                            for (Actor actor : threadActors) {
                                results[actor.ind] = caller.call(actor.method, actor.args);
                            }
                        }
                    };

                    futures.add(pool.submit(r));
                }

                for (Future<?> future : futures) {
                    try {
                        future.get();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                }
                barrier.reset();


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
            for (int i = 0; i < cntLinear.length; i++) {
                System.out.printf("%d ", cntLinear[i]);
            }
            System.out.println();
            System.out.println();
            if (errorFound) {
                break;
            }
        }
        pool.shutdown();
        System.out.println("finish");
    }
}
