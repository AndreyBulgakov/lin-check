package com.devexperts.dxlab.lincheck.strategy;

import com.devexperts.dxlab.lincheck.Utils;
// TODO use 'optimize imports' feature before commit
import java.util.Random;

// TODO javadoc
public interface Strategy {

    // TODO javadoc
    void onSharedVariableAccess(int location);
}
