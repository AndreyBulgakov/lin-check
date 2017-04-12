package com.devexperts.dxlab.lincheck.strategy;


import co.paralleluniverse.fibers.Fiber;
import com.devexperts.dxlab.lincheck.LinCheckThread;
import com.devexperts.dxlab.lincheck.Result;

import java.util.ArrayList;

/**
 * Current strategy container
 */
public abstract class StrategyHolder {
    private static Strategy currentStrategy;
    public static ArrayList<LinCheckThread> threads = new ArrayList<>();

    public static void setCurrentStrategy(Strategy curentStrategy) {
        StrategyHolder.currentStrategy = curentStrategy;
    }

    public final static ArrayList<Fiber<Result[]>> fibers = new ArrayList<>();

    @SuppressWarnings("unused") // invoked from transformed code
    public static Strategy getCurrentStrategy() {
        return currentStrategy;
    }

}
