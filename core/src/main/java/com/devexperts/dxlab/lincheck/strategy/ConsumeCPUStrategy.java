package com.devexperts.dxlab.lincheck.strategy;

import com.devexperts.dxlab.lincheck.Utils;

import java.util.Random;

/**
 * Strategy that call Utils.consumeCPU in each onSharedVariableAccess
 * @see Utils
 */
public class ConsumeCPUStrategy implements Strategy {
    private final Random random = new Random();
    private final int maxTokens;

    public ConsumeCPUStrategy(int maxTokens) {
        this.maxTokens = maxTokens;
    }


    @Override
    public void onSharedVariableRead(int location) {
        Utils.consumeCPU(random.nextInt(maxTokens));

    }

    @Override
    public void onSharedVariableWrite(int location) {
        Utils.consumeCPU(random.nextInt(maxTokens));
    }
}
