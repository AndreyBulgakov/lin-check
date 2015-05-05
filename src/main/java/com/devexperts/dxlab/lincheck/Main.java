package com.devexperts.dxlab.lincheck;

import com.devexperts.dxlab.lincheck.tests.custom.queue.QueueWithoutAnySync;
import com.devexperts.dxlab.lincheck.tests.custom.QueueCaller;
import com.devexperts.dxlab.lincheck.util.Caller;

public class Main {
    public static void main(String[] args) throws NoSuchMethodException {
//        Caller caller = new QueueCaller(AccountsSynchronized.class);

        Caller caller = new QueueCaller(QueueWithoutAnySync.class);
        Checker checker = new Checker();
        System.out.println(checker.check(caller));
    }
}

