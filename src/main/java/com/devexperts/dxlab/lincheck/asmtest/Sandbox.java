package com.devexperts.dxlab.lincheck.asmtest;

import com.devexperts.dxlab.lincheck.tests.custom.queue.Queue;
import com.devexperts.dxlab.lincheck.tests.custom.queue.QueueWithoutAnySync;
import com.devexperts.dxlab.lincheck.util.Result;
import org.objectweb.asm.util.ASMifier;

import java.util.Arrays;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class Sandbox {
    Queue q;

    public void reload() {
        q = new QueueWithoutAnySync(1);
    }


    public static void main(String[] args) throws Exception {
        Sandbox t = new Sandbox();
        Result[] res = new Result[4];

        final CyclicBarrier barrier = new CyclicBarrier(2);
        final CyclicBarrier barrierEnd = new CyclicBarrier(3);

        final Generated10 gen1 = new Generated10();
        final Generated20 gen2 = new Generated20();

        ASMifier.main(new String[]{"com.devexperts.dxlab.lincheck.asmtest.Generated1"});

        System.exit(1);

        for (int iter = 0; iter < 1; iter++) {

            Queue q = new QueueWithoutAnySync(3);
//            gen1.queue = q;
//            gen2.queue = q;

            for (int i = 0; i < 4; i++) {
                res[i] = new Result();
            }

            Runnable r1 = new Runnable() {
                @Override
                public void run() {
                    try {
                        barrier.await();

//                        gen1.process(res);

                        barrierEnd.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (BrokenBarrierException e) {
                        e.printStackTrace();
                    }

                }
            };
            Runnable r2 = new Runnable() {
                @Override
                public void run() {
                    try {
                        barrier.await();

//                        gen2.process(res);

                        barrierEnd.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (BrokenBarrierException e) {
                        e.printStackTrace();
                    }
                }
            };


            barrier.reset();
            barrierEnd.reset();

            new Thread(r1).start();
            new Thread(r2).start();

            barrierEnd.await();

            System.out.println(Arrays.toString(res));

        }
    }
}
