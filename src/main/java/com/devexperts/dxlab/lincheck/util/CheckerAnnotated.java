package com.devexperts.dxlab.lincheck.util;

import com.devexperts.dxlab.lincheck.annotations.ActorAnn;
import com.devexperts.dxlab.lincheck.annotations.CTest;
import com.devexperts.dxlab.lincheck.annotations.Reload;
import com.devexperts.dxlab.lincheck.annotations.Conf;
import com.sun.corba.se.impl.orbutil.CacheTable;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Phaser;

public class CheckerAnnotated {
    int COUNT_ITER;
    int COUNT_THREADS;

    public CheckerAnnotated() {
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

    private void executeActors(Actor[] actors, Result[] result) {
        for (Actor actor : actors) {
            Method m = methodsActor.get(actor.method);
            try {
                m.invoke(testObject, result[actor.ind], actor.args);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    private Result[] executeActors(Actor[] actors) {
        Result[] result = new Result[actors.length];
        for (int i = 0; i < actors.length; i++) {
            result[i] = new Result();
        }
        executeActors(actors, result);
        return result;
    }

    private Result[][] executeLinear(Actor[][] actors, int countActors) throws InvocationTargetException, IllegalAccessException {
        Actor[][] perms = genPermutations(actors, countActors);

        // print all possible executions
        for (Actor[] perm : perms) {
            System.out.println(Arrays.toString(perm));
        }
        System.out.println();

        Result[][] results = new Result[perms.length][];
        for (int i = 0; i < perms.length; i++) {
            reloadTestObject();
            results[i] = executeActors(perms[i]);
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


    private Object testObject;
    private List<Method> methodsActor;
    private Method methodReload;

    private void reloadTestObject() throws InvocationTargetException, IllegalAccessException {
        methodReload.invoke(testObject);
    }

    private static Interval parseInterval(String s) {
        String[] split = s.split(":");
        int from = Integer.valueOf(split[0]);
        int to = Integer.valueOf(split[1]);
        return new Interval(from, to);
    }

    public boolean checkAnnotated(Object test) throws InvocationTargetException, IllegalAccessException {
        this.testObject = test;
        Class clz = test.getClass();

        Method[] ms = clz.getDeclaredMethods();

        methodsActor = new ArrayList<>();
        methodReload = null;

        for (Method method : ms) {
            if (method.isAnnotationPresent(ActorAnn.class)) {
                methodsActor.add(method);
            }
            if (method.isAnnotationPresent(Reload.class)) {
                methodReload = method;
            }
        }

        int ind = 0;
        List<ActorGenerator> gens = new ArrayList<>();
        for (Method m : methodsActor) {
            String[] args = m.getAnnotation(ActorAnn.class).args();
            String name = m.getAnnotation(ActorAnn.class).name();
            Interval[] ivs = new Interval[args.length];

            for (int i = 0; i < args.length; i++) {
                ivs[i] = parseInterval(args[i]);
            }

            ActorGenerator gen = new ActorGenerator(ind++, name, ivs);
            gens.add(gen);
        }


        List<CheckerConfiguration> confs = new ArrayList<>();
        Annotation[] ctests = clz.getAnnotationsByType(CTest.class);
        for (Annotation ann : ctests) {
            if (ann instanceof CTest) {
                CTest ctest = (CTest) ann;

                CheckerConfiguration conf = new CheckerConfiguration();

                conf.setNumIterations(ctest.iter());
                for (String s : ctest.actorsPerThread()) {
                    conf.addThread(parseInterval(s));
                }
                for (ActorGenerator gen : gens) {
                    conf.addActorGenerator(gen);
                }

                confs.add(conf);
            }

        }

        for (CheckerConfiguration conf : confs) {
            System.out.println(conf);
            if (!checkImpl(conf)) {
                return false;
            }
        }

        return true;
    }


    private boolean checkImpl(CheckerConfiguration conf) throws InvocationTargetException, IllegalAccessException {
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
                System.out.println(Arrays.toString(actor));
            }
            System.out.println();

            Result[][] linearResults = executeLinear(actors, countActors);
            // print linear results
            System.out.println();
            for (Result[] linearResult : linearResults) {
                System.out.println(Arrays.toString(linearResult));
            }
            System.out.println();


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

                        executeActors(threadActors, results);

                        phaser.arrive();
                    }
                };
            }

            int[] cntLinear = new int[linearResults.length];
            for (int threads_num = 0; threads_num < 100000; threads_num++) {
                if (threads_num % 10000 == 0) {
                    System.out.printf("%d ", threads_num);
                }

                reloadTestObject();
                for (Runnable r : runnables) {
                    pool.execute(r);
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
                    System.out.println(Arrays.toString(results));
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
