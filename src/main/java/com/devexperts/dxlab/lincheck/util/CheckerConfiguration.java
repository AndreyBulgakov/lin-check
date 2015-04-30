package com.devexperts.dxlab.lincheck.util;

import java.util.ArrayList;
import java.util.List;

public class CheckerConfiguration {
    private int numThreads;

    private int numIterations;

    private List<Interval> rangeActorCount;
    private List<ActorGenerator> actorGenerators;
    private int indActor;

    public CheckerConfiguration(int numThreads, int numIterations, List<Interval> rangeActorCount, List<ActorGenerator> actorGenerators) {
        this.numThreads = numThreads;
        this.numIterations = numIterations;
        this.rangeActorCount = rangeActorCount;
        this.actorGenerators = actorGenerators;
    }

    public CheckerConfiguration() {
        numThreads = 0;
        numIterations = 0;
        rangeActorCount = new ArrayList<>();
        actorGenerators = new ArrayList<>();
    }

    public CheckerConfiguration setNumIterations(int n) {
        numIterations = n;
        return this;
    }

    public CheckerConfiguration addThread(Interval i) {
        numThreads++;
        rangeActorCount.add(i);
        return this;
    }

    public CheckerConfiguration addActorGenerator(ActorGenerator ag) {
        actorGenerators.add(ag);
        return this;
    }

    private ActorGenerator randomGenerator() {
        return actorGenerators.get(MyRandom.nextInt(actorGenerators.size()));
    }

    private Actor[] generateActorsArray(Interval count) {
        int countActors = MyRandom.fromInterval(count);

        Actor[] actors = new Actor[countActors];
        for (int i = 0; i < countActors; i++) {
            actors[i] = randomGenerator().generate(indActor++);
        }

        return actors;
    }

    public int getNumIterations() {
        return numIterations;
    }

    public int getNumThreads() {
        return numThreads;
    }

    public Actor[][] generateActors() {
        indActor = 0;

        Actor[][] result = new Actor[numThreads][];

        for (int i = 0; i < numThreads; i++) {
            result[i] = generateActorsArray(rangeActorCount.get(i));
        }

        return result;
    }

    @Override
    public String toString() {
        return "CheckerConfiguration{" +
                "numThreads=" + numThreads +
                ", numIterations=" + numIterations +
                ", rangeActorCount=" + rangeActorCount +
                ", actorGenerators=" + actorGenerators +
                ", indActor=" + indActor +
                '}';
    }
}
