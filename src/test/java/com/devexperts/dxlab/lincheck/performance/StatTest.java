package com.devexperts.dxlab.lincheck.performance;

import com.devexperts.dxlab.lincheck.CheckerAnnotatedASM;
import com.devexperts.dxlab.lincheck.StatData;
import com.devexperts.dxlab.lincheck.tests.custom.counter.CounterTest1;
import com.devexperts.dxlab.lincheck.tests.custom.queue.WrapperQueueWrong1;
import com.devexperts.dxlab.lincheck.tests.custom.transfer.AccountsTest1;
import com.devexperts.dxlab.lincheck.tests.custom.transfer.AccountsTest4;
import com.devexperts.dxlab.lincheck.tests.high_scale_lib.BitVectorTest1;
import com.devexperts.dxlab.lincheck.util.MyRandom;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static junit.framework.TestCase.assertTrue;

public class StatTest {

    public static PrintWriter writer;

    public static void open() throws IOException {
        writer = new PrintWriter(new BufferedWriter(new FileWriter("stat.txt", true)));
    }

    public static void close() {
        writer.close();
    }


    public static void calsStat(Object test) throws Exception {


        open();
        writer.println();
        writer.println(test.getClass());
        writer.println();
        close();

        StatData.clear();
        for (int i = 0; i < 10; i++) {
            Thread.sleep(5000);

            open();
            writer.print(i); writer.print(" ");
            close();

            CheckerAnnotatedASM checker = new CheckerAnnotatedASM();
            boolean res = checker.checkAnnotated(test);
        }

        open();
        writer.println();
        StatData.print(writer);
        close();

    }


    public static void testAll() throws Exception {

//        Counter
//        calsStat(new CounterTest1());

//        Queue
//        calsStat(new WrapperQueueWrong1());

//        Accounts
        calsStat(new AccountsTest4());
//
//        NonBlockingSetInt
        calsStat(new com.devexperts.dxlab.lincheck.tests.high_scale_lib.BitVectorTest1());
//
//        NonBlockingHashSet
        calsStat(new com.devexperts.dxlab.lincheck.tests.high_scale_lib.SetCorrect1());
//
//        MpmcArrayQueue(2)
//        calsStat(new com.devexperts.dxlab.lincheck.tests.jctools.IQueueCorrect2());
//
//        GenericMPMCQueue(2)
        calsStat(new com.devexperts.dxlab.lincheck.tests.zchannel.QueueCorrect1());
//
//        GenericMPMCQueue(16)
        calsStat(new com.devexperts.dxlab.lincheck.tests.zchannel.QueueCorrect2());
//
//        LockFreeQueue
        calsStat(new com.devexperts.dxlab.lincheck.tests.lockfreequeue.QueueCorrect1());

        //        MpmcArrayQueue(2)
        calsStat(new com.devexperts.dxlab.lincheck.tests.jctools.IQueueCorrect2());
    }

    public static void main(String[] args) throws Exception {
//        writer = new PrintWriter("stat.txt", "UTF-8");
        PrintWriter writer = new PrintWriter("stat.txt");
        writer.print("");
        writer.close();


        Random rand = new Random();
        MyRandom.r.setSeed(rand.nextLong());

        testAll();
    }
}
