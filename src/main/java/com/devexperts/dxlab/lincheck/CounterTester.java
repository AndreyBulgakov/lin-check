package com.devexperts.dxlab.lincheck;

import com.devexperts.dxlab.lincheck.counter.CounterSynchronized;
import com.devexperts.dxlab.lincheck.counter.CounterWithoutAnySync;
import com.devexperts.dxlab.lincheck.util.Actor;
import com.devexperts.dxlab.lincheck.util.MyRandom;
import com.devexperts.dxlab.lincheck.util.Result;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// TODO use main Tester class
public class CounterTester {

    private void genHelper(List<Actor[]> result, Actor[] out, int countUsed, int countActors, Actor[][] actors, int[] inds) {
        if (countActors == countUsed) {
            result.add(out.clone());
        } else {
            for (int i = 0; i < actors.length; i++) {
                if (actors[i].length != inds[i]) {
                    Actor call = actors[i][inds[i]];
                    inds[i]++;
                    out[countUsed] = call;
                    countUsed++;

                    genHelper(result, out, countUsed, countActors, actors, inds);

                    countUsed--;
                    inds[i]--;
                }
            }
        }
    }

    private Actor[][] genPermutations(Actor[][] actors, int countActors) {
        Actor[] out = new Actor[countActors];
        int[] inds = new int[]{0, 0};
        List<Actor[]> perms = new ArrayList<>();
        genHelper(perms, out, 0, countActors, actors, inds);

        return perms.toArray(new Actor[perms.size()][]);
    }

    private Result[][] linearExecution(Actor[][] perms) {
        final Result[][] results = new Result[perms.length][];

        for (int i = 0; i < perms.length; i++) {
            Actor[] localActors = perms[i];
            Result[] localResult = new Result[localActors.length];

            CounterCaller caller = new CounterCaller(new CounterWithoutAnySync());

            for (Actor localActor : localActors) {
                localResult[localActor.ind] = caller.call(localActor.method);
            }

            results[i] = localResult;
        }
        return results;
    }

    public void test() {
        int COUNT_ITER = 100000;
        int COUNT_THREADS = 2;

        for (int iter = 0; iter < COUNT_ITER; iter++) {
            final CounterCaller caller = new CounterCaller(new CounterSynchronized());
            Thread[] threads = new Thread[COUNT_THREADS];


            Actor[][] actors = new Actor[COUNT_THREADS][];
            int ind = 0;
            int countActors = 0;
            for (int i = 0; i < COUNT_THREADS; i++) {
                int cnt = MyRandom.nextInt(2) + 1;
                countActors += cnt;
                actors[i] = new Actor[cnt];
                for (int j = 0; j < cnt; j++) {
                    actors[i][j] = new Actor(0, ind++);
                }
            }


            Actor[][] perms = genPermutations(actors, countActors);

            Result[][] lin = linearExecution(perms);

            final Result[] results = new Result[countActors];
            for (int i = 0; i < COUNT_THREADS; i++) {
                final Actor[] threadActors = actors[i];
                threads[i] = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        for (Actor threadActor : threadActors) {
                            results[threadActor.ind] = caller.call(threadActor.method);
                        }
                    }
                });
            }

            for (int i = 0; i < COUNT_THREADS; i++) {
                threads[i].start();
            }
            for (int i = 0; i < COUNT_THREADS; i++) {
                try {
                    threads[i].join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            boolean correct = false;
            for (Result[] aLin : lin) {
                if (Arrays.equals(aLin, results)) {
                    correct = true;
                    break;
                }
            }
            if (correct) {
                System.out.println("ok");
            } else {
//                System.out.println("error!");
//
//
//                System.out.println("\nActor perms:\n");
//                for (int i = 0; i < perms.length; i++) {
//                    System.out.println(Arrays.asList(perms[i]));
//                }
//
//                System.out.println("\nResult perms:\n");
//                for (int i = 0; i < lin.length; i++) {
//                    System.out.println(Arrays.asList(lin[i]));
//                }
//
//                System.out.println("\nReal execution:\n");
//
//                for (int i = 0; i < actors.length; i++) {
//                    System.out.println(Arrays.asList(actors[i]));
//                }
                System.out.println(Arrays.asList(results));
//                break;
            }
        }
    }
}
