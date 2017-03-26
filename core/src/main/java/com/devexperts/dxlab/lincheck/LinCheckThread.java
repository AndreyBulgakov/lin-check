package com.devexperts.dxlab.lincheck;

import java.util.concurrent.FutureTask;

/**
 * Created by alexander on 21.03.17.
 */
public class LinCheckThread extends Thread {
    private final int id;


    public  LinCheckThread(int id, FutureTask<Result[]> execution) {
        super(execution);
        this.id = id;
    }

    public int getThreadId() {
        return id;
    }
}
