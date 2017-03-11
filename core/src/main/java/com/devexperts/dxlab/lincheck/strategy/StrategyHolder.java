package com.devexperts.dxlab.lincheck.strategy;

// TODO javadoc
public class StrategyHolder {
    private static Strategy currentStrategy; // TODO curent -> current

    public static void setCurrentStrategy(Strategy curentStrategy) {
        StrategyHolder.currentStrategy = curentStrategy;
    }

    public static Strategy getCurrentStrategy() {
        return currentStrategy;
    }
}
