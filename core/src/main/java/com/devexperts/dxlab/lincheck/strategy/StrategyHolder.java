package com.devexperts.dxlab.lincheck.strategy;

public class StrategyHolder {
    private static Strategy curentStrategy;

    public static void setCurrentStrategy(Strategy curentStrategy) {
        StrategyHolder.curentStrategy = curentStrategy;
    }

    public static Strategy getCurrentStrategy() {
        return curentStrategy;
    }
}
