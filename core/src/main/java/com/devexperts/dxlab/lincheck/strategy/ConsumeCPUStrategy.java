package com.devexperts.dxlab.lincheck.strategy;

import com.devexperts.dxlab.lincheck.Utils;

import java.util.Random;

public class ConsumeCPUStrategy implements Strategy {
    private final Random random = new Random();

    @Override
    public void onSharedVariableAccess(int location) {
        Utils.consumeCPU(random.nextInt(100));
    }
}
