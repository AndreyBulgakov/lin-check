package com.devexperts.dxlab.lincheck.strategy;

import com.devexperts.dxlab.lincheck.Utils;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Strategy that call {@link Utils#consumeCPU} in each onSharedVariableAccess
 */
public class ConsumeCPUStrategy implements Strategy {
    private final int maxTokens;

    public ConsumeCPUStrategy(int maxTokens) {
        this.maxTokens = maxTokens;
    }

    @Override
    public void onSharedVariableRead(int location) {
        onSharedVariableAccess();
    }

    @Override
    public void onSharedVariableWrite(int location) {
        onSharedVariableAccess();
    }

    private void onSharedVariableAccess() {
        Utils.consumeCPU(ThreadLocalRandom.current().nextInt(maxTokens));
    }
}
