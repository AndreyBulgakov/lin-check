package com.devexperts.dxlab.lincheck.strategy;

import com.devexperts.dxlab.lincheck.Utils;

import java.util.Random;

/**
 * Strategy that inserts Utils.consumeCPU before each shared variable access
 */
public class ConsumeCPUStrategy implements Strategy {
    private final Random random = new Random();
    private final int maxTokens;

    public ConsumeCPUStrategy(int maxTokens) {
        this.maxTokens = maxTokens;
    }

    @Override
    public void onSharedVariableAccess(int location) {
        Utils.consumeCPU(random.nextInt(maxTokens));
    }
}
