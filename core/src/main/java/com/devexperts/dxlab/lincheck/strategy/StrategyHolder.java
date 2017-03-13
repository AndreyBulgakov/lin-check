package com.devexperts.dxlab.lincheck.strategy;


/**
 * Current strategy container
 */
public abstract class StrategyHolder {
    private static Strategy currentStrategy;

    public static void setCurrentStrategy(Strategy curentStrategy) {
        StrategyHolder.currentStrategy = curentStrategy;
    }

    @SuppressWarnings("unused") // invoked from transformed code
    public static Strategy getCurrentStrategy() {
        return currentStrategy;
    }
}
