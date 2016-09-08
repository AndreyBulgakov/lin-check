/*
 *  Lincheck - Linearizability checker
 *  Copyright (C) 2015 Devexperts LLC
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.devexperts.dxlab.lincheck;

import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.*;

import com.devexperts.dxlab.lincheck.annotations.*;
import com.devexperts.dxlab.lincheck.asm.MultithreadedExecutionClass;
import com.devexperts.dxlab.lincheck.asm.MultithreadedExecutionClassGenerator;
import com.devexperts.dxlab.lincheck.generators.ParameterGenerator;
import com.devexperts.dxlab.lincheck.util.*;

public class Checker {
    boolean fullOutput;
    int COUNT_ITER;
    int COUNT_THREADS;

    public static boolean check(Object test) throws Exception {
        Checker checker = new Checker();
        return checker.checkAnnotated(test);
    }


    public Checker() {
        try {
            File file = new File("config.properties");
            FileInputStream fileInput = new FileInputStream(file);
            Properties prop = new Properties();
            prop.load(fileInput);
            fileInput.close();

            fullOutput = Boolean.parseBoolean(prop.getProperty("fullOutput"));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
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

    private static Result[] generateEmptyResults(int n) {
        Result[] res = new Result[n];
        for (int i = 0; i < n; i++) {
            res[i] = new Result();
        }
        return res;
    }

    private void executeActors(Actor[] actors, Result[] result) {
        for (int i = 0; i < actors.length; i++) {
            Method m = actors[i].method;
            try {
                Object[] test = new Object[actors[i].args.length];
                for (int j = 0; j < test.length; j++) {
                    test[j] = actors[i].args[j];
                }
                if (actors[i].method.getReturnType().getName().equals("void")) {
                    m.invoke(testObject, test);
                    result[i].setVoid();
                } else {
                    result[i].setValue(m.invoke(testObject, test));
                }
            } catch (IllegalAccessException | InvocationTargetException e) {
                result[i].setException((Exception) e.getCause());
            }
        }
    }

    private Result[] executeActors(Actor[] actors) {
        Result[] result = generateEmptyResults(actors.length);
        executeActors(actors, result);
        return result;
    }

    private Result[][] executeLinear(Actor[][] actorsConf, int countActors) throws InvocationTargetException, IllegalAccessException {
        Actor[][] perms = genPermutations(actorsConf, countActors);

        if (fullOutput) {
            // print all possible executions
            for (Actor[] perm : perms) {
                System.out.println(Arrays.toString(perm));
            }
            System.out.println();
        }

        Result[][] results = new Result[perms.length][];
        for (int i = 0; i < perms.length; i++) {
            Actor[] actors = perms[i];

            reloadTestObject();


            Result[] resultUnordered = executeActors(actors);


            Result[] result = new Result[resultUnordered.length];
            for (int j = 0; j < actors.length; j++) {
                result[actors[j].ind] = resultUnordered[j];
            }

            results[i] = result;
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

    private ParameterGenerator getGenerator(Param param) throws IllegalAccessException, InvocationTargetException, InstantiationException {
        Constructor[] ctors = param.generator().getConstructors();
        Constructor ctor = null;
        for (int j = 0; j < ctors.length; j++) {
            ctor = ctors[j];
            if (ctor.getGenericParameterTypes().length == param.generatorParameters().length)
                break;
        }
        ctor.setAccessible(true);
        //Object[] x = ((Param)i).generatorParameters();
        return (ParameterGenerator) ctor.newInstance(param.generatorParameters());
    }

    /**
     * use Class clz and methodsActor
     *
     * @param clz, methodActors
     * @return return Method-Argument map
     */
    private ParameterGenerator[][] getGeneratorsMap(Class clz)
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException {
        Map<String, ParameterGenerator> ParameterNameGeneratorMap = new HashMap<>();
        ParameterGenerator[][] generators = new ParameterGenerator[methodsActor.size()][];
        Annotation[] params = clz.getAnnotationsByType(Param.class);

        for (Annotation i : params) {
            ParameterNameGeneratorMap.put(((Param) i).name(), getGenerator((Param) i));
        }
        for (int j = 0; j < methodsActor.size(); j++) {
            Parameter[] methodParameters = methodsActor.get(j).getParameters();
            Operation methodAnnotation = methodsActor.get(j).getAnnotation(Operation.class);
            ParameterGenerator[] methodParams = new ParameterGenerator[methodParameters.length];
            if (methodAnnotation.params().length == 0) {
                for (int i = 0; i < methodParameters.length; i++) {
                    Param p = methodParameters[i].getAnnotation(Param.class);
                    if (p.name().equals("")) {
                        methodParams[i] = getGenerator(p);
                    } else {
                        methodParams[i] = ParameterNameGeneratorMap.get(p.name());
                    }
                }
            } else {
                String[] parametersNames = methodAnnotation.params();
                for (int i = 0; i < methodParameters.length; i++) {
                    methodParams[i] = ParameterNameGeneratorMap.get(parametersNames[i]);
                }
            }
            generators[j] = methodParams;
        }
        return generators;
    }

    long startTime;

    public boolean checkAnnotated(Object test) throws Exception {
        this.testObject = test;
        Class clz = test.getClass();
        Annotation[] ctests = clz.getAnnotationsByType(CTest.class);

        Method[] ms = clz.getDeclaredMethods();

        methodsActor = new ArrayList<>();
        methodReload = null;

        for (Method method : ms) {
            if (method.isAnnotationPresent(Operation.class)) {
                methodsActor.add(method);
            }
            if (method.isAnnotationPresent(Reset.class)) {
                methodReload = method;
            }
        }

        int ind = 0;
        List<ActorGenerator> gens = new ArrayList<>();
        ParameterGenerator[][] argsMap = getGeneratorsMap(clz);
        for (int i = 0; i < methodsActor.size(); i++) {
            boolean isMutable = !(methodsActor.get(i).isAnnotationPresent(ReadOnly.class));
            ActorGenerator gen = new ActorGenerator(ind++, methodsActor.get(i), argsMap[i]);
            gen.setMutable(isMutable);
            gens.add(gen);
        }

        List<CheckerConfiguration> confs = new ArrayList<>();
        for (Annotation ann : ctests) {
            if (ann instanceof CTest) {
                CTest ctest = (CTest) ann;

                CheckerConfiguration conf = new CheckerConfiguration();

                conf.setNumIterations(ctest.iter());
                conf.addThreads(ctest.actorsPerThread());
                for (ActorGenerator i : gens) {
                    conf.addActorGenerator(i);
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

    MultithreadedExecutionClass[] multithreadedExecutionClasses;
    Object[][][] argumentMatrix;

    private boolean checkImpl(CheckerConfiguration conf) throws Exception {
        long sumTime = 0;
        double sumDisp = 0;


        COUNT_ITER = conf.getNumIterations();
        COUNT_THREADS = conf.getNumThreads();

        ExecutorService pool = Executors.newFixedThreadPool(COUNT_THREADS);
        final Phaser phaser = new Phaser(COUNT_THREADS + 1);

        boolean errorFound = false;
        int errorIter = -1;
        int errorConcurIter = -1;
        for (int iter = 0; iter < COUNT_ITER; iter++) {
            System.out.println("iter = " + iter);

            CheckerConfiguration cloneConf = conf.clone();
            Actor[][] actors = cloneConf.generateActors(true);

            int countActors = 0;
            for (Actor[] actor : actors) {
                countActors += actor.length;
            }

            if (fullOutput) {
                System.out.println("Thread configuration:");
                for (Actor[] actor : actors) {
                    System.out.println(Arrays.toString(actor));
                }
                System.out.println();
            }

            // generate classes
            //String testClassName = testObject.getClass().getCanonicalName();
            //testClassName = testClassName.replace(".", "/");
            multithreadedExecutionClasses = new MultithreadedExecutionClass[COUNT_THREADS];
            argumentMatrix = new Object[COUNT_THREADS][][];
            for (int i = 0; i < actors.length; i++) {
                Actor[] actor = actors[i];

                String generatedClassName = "com.devexperts.dxlab.lincheck.asm.MultithreadedExecutionClass" + i;

                Method[] methods = new Method[actor.length];
                Object[][] argForThread = new Object[actor.length][];
                for (int j = 0; j < actor.length; j++) {
                    methods[j] = actor[j].method;
                    argForThread[j] = actor[j].args;
                }

                multithreadedExecutionClasses[i] = MultithreadedExecutionClassGenerator.generate(
                        testObject,
                        phaser,
                        generatedClassName,
                        generatedClassName.replace('.', '/'),
                        methods,
                        argForThread
                );
                argumentMatrix[i] = argForThread;
            }


            Result[][] linearResults = executeLinear(actors, countActors);
            if (fullOutput) {
                // print linear results
                System.out.println();
                for (Result[] linearResult : linearResults) {
                    System.out.println(Arrays.toString(linearResult));
                }
                System.out.println();
            }


            if (fullOutput) {
                System.out.println("Progress:");
                System.out.printf("[%d] ", 100000);
            }

            Result[][] results = new Result[COUNT_THREADS][];
            for (int i = 0; i < COUNT_THREADS; i++) {
                results[i] = generateEmptyResults(actors[i].length);
            }

            int[][] waits = new int[COUNT_THREADS][];
            for (int i = 0; i < COUNT_THREADS; i++) {
                waits[i] = new int[actors[i].length];
            }

            Runnable[] runnables = new Runnable[COUNT_THREADS];
            for (int i = 0; i < COUNT_THREADS; i++) {
                final Result[] threadResult = results[i];
                final Object[][] threadArgs = argumentMatrix[i];
                final MultithreadedExecutionClass classGen = multithreadedExecutionClasses[i];
                final int[] localWaits = waits[i];
                runnables[i] = new Runnable() {
                    @Override
                    public void run() {
                        classGen.process(threadResult, threadArgs, localWaits);
                        //phaser.arrive();
                    }
                };
            }

            Result[] resultOrdered = new Result[countActors];
            int[] cntLinear = new int[linearResults.length];
            for (int threads_num = 0; threads_num < 100_000; threads_num++) {
                if (fullOutput && threads_num % 10000 == 0) {
                    System.out.printf("%d ", threads_num);
                }

                reloadTestObject();


                if (threads_num > 50_000) {
                    for (int i = 0; i < COUNT_THREADS; i++) {
                        for (int j = 0; j < waits[i].length; j++) {
                            waits[i][j] = (int) (MyRandom.nextLong() % 10_000L);
                        }
                    }
                }

                long stT = System.currentTimeMillis();
                for (Runnable r : runnables) {
                    pool.execute(r);
                }

                phaser.arriveAndAwaitAdvance();
                phaser.arriveAndAwaitAdvance();
                sumTime += (System.currentTimeMillis() - stT);

                for (int i = 0; i < COUNT_THREADS; i++) {
                    for (int j = 0; j < actors[i].length; j++) {
                        resultOrdered[actors[i][j].ind] = results[i][j];
                    }
                }


                boolean correct = false;
                for (int i = 0; i < linearResults.length; i++) {
                    if (Arrays.equals(linearResults[i], resultOrdered)) {
                        correct = true;
                        cntLinear[i]++;
                        break;
                    }
                }
                if (!correct) {
                    System.out.println("Error found.");


                    if (!fullOutput) {
                        System.out.println("Thread configuration:");
                        for (Actor[] actor : actors) {
                            System.out.println(Arrays.toString(actor));
                        }
                        System.out.println();

                        Actor[][] perms = genPermutations(actors, countActors);

                        // print all possible executions
                        System.out.println("Permutations:");
                        for (Actor[] perm : perms) {
                            System.out.println(Arrays.toString(perm));
                        }
                        System.out.println();

                        // print linear results
                        System.out.println("Possible results");
                        System.out.println();
                        for (Result[] linearResult : linearResults) {
                            System.out.println(Arrays.toString(linearResult));
                        }
                        System.out.println();
                    }

                    System.out.println();
                    System.out.println("Unexpected result:");
                    System.out.println(Arrays.toString(resultOrdered));
                    errorFound = true;
                    errorConcurIter = threads_num;
                    break;
                }
            }
            if (fullOutput) {
                System.out.println();
            }
            System.out.printf("Histogram: ");
            long A, B;
            A = 0;
            B = 0;
            for (int cnt : cntLinear) {
                System.out.printf("%d ", cnt);
                A += cnt * cnt;
                B += cnt;
            }
            double disp = ((A) - (B * B / 1.0 / cntLinear.length)) / (cntLinear.length);
            disp = Math.sqrt(Math.abs(disp));
            sumDisp += disp;

            System.out.println();
            System.out.println();
            if (errorFound) {
                errorIter = iter;
                break;
            }
        }
        pool.shutdown();


        long timeDelta = System.currentTimeMillis() - startTime;

        errorIter++;
        errorConcurIter++;

        System.out.println(" === Test info begin");
        System.out.println("    Total time: " + timeDelta);
        System.out.println("    errorIter = " + errorIter);
        System.out.println("    errorConcurIter = " + errorConcurIter);
        System.out.println(" === Test info end");

        if (errorFound) {
            StatData.addIter(errorIter);
            StatData.addConcur(errorConcurIter);
            StatData.addTime(timeDelta);
        } else {
            StatData.addIter(-1);
            StatData.addConcur(-1);
            StatData.addTime(-1);
        }


        System.out.println("finish");
        System.out.println("sumTime = " + sumTime);
        System.out.println("sumDisp = " + sumDisp);
        return !errorFound;
    }
}
