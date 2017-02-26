package com.devexperts.dxlab.lincheck.strategy;

import com.devexperts.dxlab.lincheck.Utils;

import java.util.Random;

public interface Strategy {

    void onSharedVariableAccess(int location);

}
