package com.devexperts.dxlab.lincheck.strategy;

// TODO javadoc
public class StrategyHolder {
    private static Strategy curentStrategy; // TODO curent -> current

    public static void setCurrentStrategy(Strategy curentStrategy) {
        StrategyHolder.curentStrategy = curentStrategy;
    }

    public static Strategy getCurrentStrategy() {
        return curentStrategy;
    }
}
