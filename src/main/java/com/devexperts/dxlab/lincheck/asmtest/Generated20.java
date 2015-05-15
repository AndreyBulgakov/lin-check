package com.devexperts.dxlab.lincheck.asmtest;

import com.devexperts.dxlab.lincheck.tests.custom.QueueTestAnn;
import com.devexperts.dxlab.lincheck.tests.custom.queue.Queue;
import com.devexperts.dxlab.lincheck.util.Result;

public class Generated20 extends Generated {
    public QueueTestAnn queue;

    public Generated20() {
    }

    public Generated20(QueueTestAnn queue) {
        this.queue = queue;
    }

    @Override
    public void process(Result[] res, Object[][] args) {
        try {
            queue.get(res[2], args[2]);
        } catch (Exception e) {
            res[0].setException(e);
        }

        try {
            queue.put(res[3], args[3]);
        } catch (Exception e) {
            res[1].setException(e);
        }

    }
}
