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


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Current strategy container
 */
public abstract class StrategyHolder {
    private static Strategy currentStrategy;
    //    public static ArrayList<LinCheckThread> threads = new ArrayList<>();
    private static Map<ThreadGroup, Strategy> strategyMap = new ConcurrentHashMap<>();

    public static void setCurrentStrategy(Strategy curentStrategy) {
        strategyMap.put(Thread.currentThread().getThreadGroup(), curentStrategy);
//        StrategyHolder.currentStrategy = curentStrategy;
    }

    public static void setCurrentStrategy(ThreadGroup threadGroup, Strategy strategy) {
        strategyMap.put(threadGroup, strategy);
    }

    @SuppressWarnings("unused") // invoked from transformed code
    public static Strategy getCurrentStrategy() {
        final ThreadGroup currentGroup = Thread.currentThread().getThreadGroup();
//        Strategy result = strategyMap.get(currentGroup);
//        if (result != null){
//            return result;
//        }
        return strategyMap.keySet().stream()
                .filter(group -> group.parentOf(currentGroup))
                .findFirst()
                .map(group -> strategyMap.get(group))
                .orElse(null);

    }

//    @SuppressWarnings("unused") // invoked from transformed code
//    public static Strategy getCurrentStrategy() {
//        return currentStrategy;
//    }


    //Maybe it works faster
    private static Strategy getThreadGroupsStrategyOrNull() {
        ThreadGroup currentGroup = Thread.currentThread().getThreadGroup();
        Strategy currentStrategy = null;
        for (ThreadGroup parrent = Thread.currentThread().getThreadGroup();
             currentStrategy == null || parrent != null;
             parrent = parrent.getParent()) {
            if (currentGroup.getName().equals("LinCheckGroup")) {
                currentStrategy = strategyMap.get(currentGroup);
            }
            currentGroup = parrent;
        }
        return currentStrategy;
    }
}
