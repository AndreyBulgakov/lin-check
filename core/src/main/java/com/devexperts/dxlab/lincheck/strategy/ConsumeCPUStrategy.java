package com.devexperts.dxlab.lincheck.strategy;

import com.devexperts.dxlab.lincheck.Utils;

import java.util.Random;

// TODO javadoc
public class ConsumeCPUStrategy implements Strategy {
    private final Random random = new Random();

    @Override
    public void onSharedVariableAccess(int location) {
        // TODO Why 100? Add 'maxTokens' prameter to constructor
        Utils.consumeCPU(random.nextInt(100));
    }
}
