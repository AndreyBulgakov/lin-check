package com.devexperts.dxlab.lincheck.strategy;


import com.devexperts.dxlab.lincheck.LinCheckThread;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Current strategy container
 */
public abstract class StrategyHolder {
    private static Strategy currentStrategy;
    public static ArrayList<LinCheckThread> threads = new ArrayList<>();
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
