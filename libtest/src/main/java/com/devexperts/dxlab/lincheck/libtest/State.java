package com.devexperts.dxlab.lincheck.libtest;

/**
 * Created by alexander on 09.02.17.
 */
public class State {

    public int signal = 0;
    public int changes = 0;
    public int N;

    public State(int n) { N = n; }
}
