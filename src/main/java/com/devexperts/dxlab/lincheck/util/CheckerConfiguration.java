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

import java.lang.reflect.Array;
import java.util.*;

public class CheckerConfiguration implements Cloneable {
    private int numThreads;

    private int numIterations;

    private List<ActorGenerator> actorGenerators;
    private int indActor;
    private int[][] rangeActorCount;

    public CheckerConfiguration(int numThreads, int numIterations, int[][] rangeActorCount, List<ActorGenerator> actorGenerators) {
        this.numThreads = numThreads;
        this.numIterations = numIterations;
        this.rangeActorCount = rangeActorCount;
        this.actorGenerators = actorGenerators;
    }

    public CheckerConfiguration() {
        numThreads = 0;
        numIterations = 0;
        actorGenerators = new ArrayList<>();
    }

    public CheckerConfiguration setNumIterations(int n) {
        numIterations = n;
        return this;
    }

    public CheckerConfiguration addThreads(String[] intervals) {
        numThreads = intervals.length;
        rangeActorCount = new int[numThreads][];
        for (int i = 0; i < intervals.length; i++) {
            String[] split = intervals[i].split(":");
            int[] interval = {Integer.valueOf(split[0]), Integer.valueOf(split[1])};
            rangeActorCount[i] = interval;
        }
        return this;
    }

    public CheckerConfiguration addActorGenerator(ActorGenerator ag) {
        actorGenerators.add(ag);
        return this;
    }

    private ActorGenerator randomGenerator() {
        return actorGenerators.get(MyRandom.nextInt(actorGenerators.size()));
    }

    private Actor[] generateActorsArray(int[] count) {
        int countActors = count[0] + MyRandom.nextInt(count[1] - count[0]);

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

    public Actor[][] generateActors(boolean immutableFix) {
        indActor = 0;

        Actor[][] result = new Actor[numThreads][];

        int minCountRow = Integer.MAX_VALUE;
        for (int i = 0; i < numThreads; i++) {
            result[i] = generateActorsArray(rangeActorCount[i]);
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
    public CheckerConfiguration clone() {
        int[][] cloneIntervals = rangeActorCount.clone();;
        List<ActorGenerator> cloneActorGenerators = new ArrayList<>();
        actorGenerators.forEach(x -> cloneActorGenerators.add(x.clone()));
        return new CheckerConfiguration(numThreads, numIterations, cloneIntervals, cloneActorGenerators);
    }
}
