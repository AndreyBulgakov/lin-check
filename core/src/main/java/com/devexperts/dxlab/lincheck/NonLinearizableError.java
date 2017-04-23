package com.devexperts.dxlab.lincheck;

/**
 * Created by andrey on 4/23/17.
 */
public class NonLinearizableError extends AssertionError {

    public NonLinearizableError() {
        super("Non-linearizable execution detected, see log for details");
    }
}
