package com.devexperts.dxlab.lincheck.asmtest;

import com.devexperts.dxlab.lincheck.tests.custom.QueueTestAnn;
import com.devexperts.dxlab.lincheck.util.Result;

import java.util.Objects;

public class Generated10 extends Generated {
    public QueueTestAnn queue;

    public Generated10() {
    }

    public Generated10(QueueTestAnn queue) {
        this.queue = queue;
    }

    @Override
    public void process(Result[] res, Object[][] args) {
        try {
            queue.put(res[123], args[123]);
        } catch (Exception e) {
            res[123].setException(e);
        }

        try {
            queue.get(res[456], args[456]);
        } catch (Exception e) {
            res[456].setException(e);
        }

    }
}
