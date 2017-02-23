package com.devexperts.dxlab.lincheck;

import java.util.Random;

public interface Strategy {

    void onSharedVariableAccess(int location);

    class ThreadYeldStrategy implements Strategy {

        @Override
        public void onSharedVariableAccess(int locationId) {
            if (new Random().nextBoolean())
                Thread.yield();
        }
    }

    class RandomWaitStrategy implements Strategy {
        final Random random = new Random(0);

        @Override
        public void onSharedVariableAccess(int locationId) {
            Utils.consumeCPU(random.nextInt(100));
        }
    }

    class StrategyHolder {
        public static Strategy currentStrategy;
    }
}
