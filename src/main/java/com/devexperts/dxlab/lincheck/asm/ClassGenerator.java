package com.devexperts.dxlab.lincheck.asm;


import com.devexperts.dxlab.lincheck.asm.templ.*;
import jdk.internal.org.objectweb.asm.*;

import java.lang.reflect.Constructor;

public class ClassGenerator implements Opcodes {

    public static Generated generate(
            Object test,
            String pointedClassName,
            String generatedClassName, // "com/devexperts/dxlab/lincheck/asmtest/Generated2"
            String testFieldName, // queue
            String testClassName, // com/devexperts/dxlab/lincheck/tests/custom/QueueTestAnn
            String[] methodNames
    ) throws Exception {
        DynamicClassLoader loader = new DynamicClassLoader();

        int n = methodNames.length;
        Class<?> clz = null;
        if (n == 1) {
            clz = loader.define(pointedClassName,
                    Generated1Dump.dump(
                            generatedClassName,
                            testFieldName,
                            testClassName,
                            methodNames
                    ));
        } else if (n == 2) {
            clz = loader.define(pointedClassName,
                    Generated2Dump.dump(
                            generatedClassName,
                            testFieldName,
                            testClassName,
                            methodNames
                    ));
        } else if (n == 3) {
            clz = loader.define(pointedClassName,
                    Generated3Dump.dump(
                            generatedClassName,
                            testFieldName,
                            testClassName,
                            methodNames
                    ));
        } else if (n == 4) {
            clz = loader.define(pointedClassName,
                    Generated4Dump.dump(
                            generatedClassName,
                            testFieldName,
                            testClassName,
                            methodNames
                    ));
        } else if (n == 5) {
            clz = loader.define(pointedClassName,
                    Generated5Dump.dump(
                            generatedClassName,
                            testFieldName,
                            testClassName,
                            methodNames
                    ));
        } else {
            throw new IllegalArgumentException("Count actor should be from 1 to 5 inclusive");
        }


        Constructor<?>[] ctors = clz.getConstructors();
        Constructor<?> ctor = ctors[1];
        Generated o = (Generated) ctor.newInstance(test);
        return o;
    }

    private static class DynamicClassLoader extends ClassLoader {
        public Class<?> define(String className, byte[] bytecode) {
            return super.defineClass(className, bytecode, 0, bytecode.length);
        }
    };
}
