package com.devexperts.dxlab.lincheck.asmtest;

import com.devexperts.dxlab.lincheck.CheckerAnnotatedASM;
import com.devexperts.dxlab.lincheck.tests.custom.QueueTestAnn;
import com.devexperts.dxlab.lincheck.util.Result;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;

public class SandboxGen {
    public static void testRun(Object test) throws Exception {
        Generated gen1 = ClassGenerator2.generate(test,
                "com.devexperts.dxlab.lincheck.asmtest.Generated1",
                "com/devexperts/dxlab/lincheck/asmtest/Generated1",
                "queue",
                "com/devexperts/dxlab/lincheck/tests/custom/QueueTestAnn",
                new String[]{"put", "get"},
                new int[]{0, 1}
        );
        Generated gen2 = ClassGenerator2.generate(test,
                "com.devexperts.dxlab.lincheck.asmtest.Generated2",
                "com/devexperts/dxlab/lincheck/asmtest/Generated2",
                "queue",
                "com/devexperts/dxlab/lincheck/tests/custom/QueueTestAnn",
                new String[]{"put", "get", "put"},
                new int[]{2, 3}
        );

        Result[] res = new Result[4];
        for (int i = 0; i < 4; i++) {
            res[i] = new Result();
        }
        Object[][] vars = new Object[4][];
        vars[0] = new Object[]{10};
        vars[1] = new Object[]{8};
        vars[2] = new Object[]{7};
        vars[3] = new Object[]{5};
//        queue.put(res[0], args[0]);
//        queue.get(res[1], args[1]);
//        queue.get(res[2], args[2]);
//        queue.put(res[3], args[3]);


        for (int i = 0; i < 4; i++) {
//            test.reload();

            Class<?> cl = test.getClass();
            Method reload = cl.getDeclaredMethod("reload");
            reload.invoke(test);


            gen1.process(res, vars);
            gen2.process(res, vars);

            System.out.println(Arrays.toString(res));
        }
    }

    public static void main(String[] args) throws Exception {
        long start = System.currentTimeMillis();

        Object test = new QueueTestAnn();

//        testRun(test);

        CheckerAnnotatedASM checker = new CheckerAnnotatedASM();

        System.out.println(checker.checkAnnotated(test));

        System.out.println(System.currentTimeMillis() - start);

    }
}
