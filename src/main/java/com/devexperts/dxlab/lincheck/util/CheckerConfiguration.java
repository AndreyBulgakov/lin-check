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

package com.devexperts.dxlab.lincheck.util;

import com.devexperts.dxlab.lincheck.Checker;

import java.util.*;

public class CheckerConfiguration implements Cloneable {
    private int numThreads;

    private int numIterations;

    private List<Interval> rangeActorCount;
    private List<ActorGenerator> actorGenerators;
    private int indActor;
    private int maxThreadNumber = 0;

    public CheckerConfiguration(int numThreads, int numIterations, List<Interval> rangeActorCount, List<ActorGenerator> actorGenerators) {
        this.numThreads = numThreads;
        this.numIterations = numIterations;
        this.rangeActorCount = rangeActorCount;
        this.actorGenerators = actorGenerators;
        for (ActorGenerator i: actorGenerators) {
            maxThreadNumber+= i.getNumberOfValidStreams();
        }
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

    private Actor[] generateActorsArray(Interval count, int numThreads) {
        for (int i = 0; i < actorGenerators.size(); i++) {
            if (actorGenerators.get(i).getNumberOfValidStreams() == 0)
                actorGenerators.remove(i);
        }
        ActorGenerator generator = null;
        if (numThreads == maxThreadNumber)
            generator = randomGenerator();
        int countActors = MyRandom.fromInterval(count);
        Set<ActorGenerator> actorGeneratorsSet = new HashSet<>();
        Actor[] actors = new Actor[countActors];
        for (int i = 0; i < countActors; i++) {
            if (numThreads != maxThreadNumber)
                generator = randomGenerator();
            actorGeneratorsSet.add(generator);
            actors[i] = generator.generate(indActor++);
        }
        actorGeneratorsSet.forEach(ActorGenerator::decNumberOfValidStreams);
        return actors;
    }

    public int getNumIterations() {
        return numIterations;
    }

    public int getNumThreads() {
        return numThreads;
    }

    public Actor[][] generateActors(boolean immutableFix) {
        indActor = 0;

        Actor[][] result = new Actor[numThreads][];

        int minCountRow = Integer.MAX_VALUE;
        for (int i = 0; i < numThreads; i++) {
            result[i] = generateActorsArray(rangeActorCount.get(i), numThreads);
            minCountRow = Math.min(minCountRow, result[i].length);
        }

        if (immutableFix) {
            for (int row = 0; row < minCountRow; row++) {
                boolean allImmutable = true;
                for (int i = 0; i < numThreads; i++) {
                    if (result[i][row].isMutable) {
                        allImmutable = false;
                        break;
                    }
                }
                if (allImmutable) {
                    int ind = MyRandom.nextInt(numThreads);
                    while (!result[ind][row].isMutable) {
                        result[ind][row] = randomGenerator().generate(result[ind][row].ind);
                    }
                }
            }
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
    @Override
    public CheckerConfiguration clone(){
        List<Interval> cloneIntervals = new ArrayList<>();
        rangeActorCount.forEach(x -> cloneIntervals.add(x.clone()));
        List<ActorGenerator> cloneActorGenerators = new ArrayList<>();
        actorGenerators.forEach(x -> cloneActorGenerators.add(x.clone()));
        return new CheckerConfiguration(numThreads, numIterations, cloneIntervals, cloneActorGenerators);
    }
}
