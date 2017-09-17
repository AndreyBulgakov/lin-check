package com.devexperts.dxlab.lincheck.strategy;

/*
 * #%L
 * core
 * %%
 * Copyright (C) 2015 - 2017 Devexperts, LLC
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */


import co.paralleluniverse.strands.Strand;
import com.devexperts.dxlab.lincheck.ExecutionsStrandPool;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Current strategy container
 */
public abstract class StrategyHolder {
    private static Strategy currentStrategy;

    public static final Map<ThreadGroup, Strategy> strategyMap = new ConcurrentHashMap<>();
    private static final Map<Integer, Strategy> strategyIntMap = new ConcurrentHashMap<>();
    private static final Strategy dummy = new DummyStrategy();



    public static void setCurrentStrategy(int iteration, Strategy curentStrategy) {
        strategyIntMap.put(iteration, curentStrategy);
    }

    @SuppressWarnings("unused") // invoked from transformed code
    public static Strategy getCurrentStrategy(){
        Strand currentStrand = Strand.currentStrand();
        if (currentStrand instanceof ExecutionsStrandPool.ExecutionFiber){
            ExecutionsStrandPool.ExecutionFiber fiber = (ExecutionsStrandPool.ExecutionFiber) currentStrand;
            return strategyIntMap.get(fiber.getIteration());
        }
        Thread currentThread = Thread.currentThread();
        if (currentThread instanceof ExecutionsStrandPool.ExecutionThread){
            ExecutionsStrandPool.ExecutionThread thread = (ExecutionsStrandPool.ExecutionThread) currentThread;
            return strategyIntMap.get(thread.getIteration());
        }
        return dummy;
    }
//
//    @SuppressWarnings("unused") // invoked from transformed code
//    public static Strategy getCurrentStrategy() {
//        final ThreadGroup currentGroup = Thread.currentThread().getThreadGroup();
//        return strategyMap.keySet().stream()
//                .filter(group -> group.parentOf(currentGroup))
//                .findFirst()
//                .map(group -> strategyMap.get(group))
//                .orElse(null);
//
//    }

//    //Maybe it works faster
//    private static Strategy getThreadGroupsStrategyOrNull() {
//        ThreadGroup currentGroup = Thread.currentThread().getThreadGroup();
//        Strategy currentStrategy = null;
//        for (ThreadGroup parrent = Thread.currentThread().getThreadGroup();
//             currentStrategy == null || parrent != null;
//             parrent = parrent.getParent()) {
//            if (currentGroup.getName().equals("LinCheckGroup")) {
//                currentStrategy = strategyMap.get(currentGroup);
//            }
//            currentGroup = parrent;
//        }
//        return currentStrategy;
//    }
}
