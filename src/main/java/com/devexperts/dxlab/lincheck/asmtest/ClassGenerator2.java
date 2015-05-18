package com.devexperts.dxlab.lincheck.asmtest;


import jdk.internal.org.objectweb.asm.*;

import java.lang.reflect.Constructor;

public class ClassGenerator2 implements Opcodes {

    public static Generated generate(
            Object test,
            String pointedClassName,
            String generatedClassName, // "com/devexperts/dxlab/lincheck/asmtest/Generated2"
            String testFieldName, // queue
            String testClassName, // com/devexperts/dxlab/lincheck/tests/custom/QueueTestAnn
            String[] methodNames,
            int[] methodInds
    ) throws Exception {
        DynamicClassLoader loader = new DynamicClassLoader();

        Class<?> helloWorldClass = loader.define(pointedClassName,
                dump(
                        generatedClassName,
                        testFieldName,
                        testClassName,
                        methodNames,
                        methodInds
                ));


        Constructor<?>[] ctors = helloWorldClass.getConstructors();
        Constructor<?> ctor = ctors[1];
        Generated o = (Generated) ctor.newInstance(test);
        return o;
    }

    private static class DynamicClassLoader extends ClassLoader {
        public Class<?> define(String className, byte[] bytecode) {
            return super.defineClass(className, bytecode, 0, bytecode.length);
        }
    };

    private static byte[] dump(
            String generatedClassName, // "com/devexperts/dxlab/lincheck/asmtest/Generated2"
            String testFieldName, // queue
            String testClassName, // com/devexperts/dxlab/lincheck/tests/custom/QueueTestAnn
            String[] methodNames,
            int[] methodInds
    ) {

        ClassWriter cw = new ClassWriter(0);
        FieldVisitor fv;
        MethodVisitor mv;
        AnnotationVisitor av0;

        cw.visit(52, ACC_PUBLIC + ACC_SUPER, generatedClassName, null, "com/devexperts/dxlab/lincheck/asmtest/Generated", null);

        {
            fv = cw.visitField(ACC_PUBLIC, testFieldName, "L" + testClassName + ";", null, null);
            fv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKESPECIAL, "com/devexperts/dxlab/lincheck/asmtest/Generated", "<init>", "()V", false);
            mv.visitInsn(RETURN);
            mv.visitMaxs(1, 1);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC, "<init>", "(L" + testClassName + ";)V", null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKESPECIAL, "com/devexperts/dxlab/lincheck/asmtest/Generated", "<init>", "()V", false);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitFieldInsn(PUTFIELD, generatedClassName, testFieldName, "L" + testClassName + ";");
            mv.visitInsn(RETURN);
            mv.visitMaxs(2, 2);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC, "process", "([Lcom/devexperts/dxlab/lincheck/util/Result;[[Ljava/lang/Object;)V", null, null);
            mv.visitCode();
            Label l0 = new Label();
            Label l1 = new Label();
            Label l2 = new Label();
            mv.visitTryCatchBlock(l0, l1, l2, "java/lang/Exception");
            Label l3 = new Label();
            Label l4 = new Label();
            Label l5 = new Label();
            mv.visitTryCatchBlock(l3, l4, l5, "java/lang/Exception");
            mv.visitLabel(l0);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, generatedClassName, testFieldName, "L" + testClassName + ";");
            mv.visitVarInsn(ALOAD, 1);
            mv.visitIntInsn(BIPUSH, methodInds[0]);
            mv.visitInsn(AALOAD);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitIntInsn(BIPUSH, methodInds[0]);
            mv.visitInsn(AALOAD);
            mv.visitMethodInsn(INVOKEVIRTUAL, testClassName, methodNames[0], "(Lcom/devexperts/dxlab/lincheck/util/Result;[Ljava/lang/Object;)V", false);
            mv.visitLabel(l1);
            mv.visitJumpInsn(GOTO, l3);
            mv.visitLabel(l2);
            mv.visitFrame(Opcodes.F_SAME1, 0, null, 1, new Object[]{"java/lang/Exception"});
            mv.visitVarInsn(ASTORE, 3);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitIntInsn(BIPUSH, methodInds[0]);
            mv.visitInsn(AALOAD);
            mv.visitVarInsn(ALOAD, 3);
            mv.visitMethodInsn(INVOKEVIRTUAL, "com/devexperts/dxlab/lincheck/util/Result", "setException", "(Ljava/lang/Exception;)V", false);
            mv.visitLabel(l3);
            mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, generatedClassName, testFieldName, "L" + testClassName + ";");
            mv.visitVarInsn(ALOAD, 1);
            mv.visitIntInsn(SIPUSH, methodInds[1]);
            mv.visitInsn(AALOAD);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitIntInsn(SIPUSH, methodInds[1]);
            mv.visitInsn(AALOAD);
            mv.visitMethodInsn(INVOKEVIRTUAL, testClassName, methodNames[1], "(Lcom/devexperts/dxlab/lincheck/util/Result;[Ljava/lang/Object;)V", false);
            mv.visitLabel(l4);
            Label l6 = new Label();
            mv.visitJumpInsn(GOTO, l6);
            mv.visitLabel(l5);
            mv.visitFrame(Opcodes.F_SAME1, 0, null, 1, new Object[] {"java/lang/Exception"});
            mv.visitVarInsn(ASTORE, 3);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitIntInsn(SIPUSH, methodInds[1]);
            mv.visitInsn(AALOAD);
            mv.visitVarInsn(ALOAD, 3);
            mv.visitMethodInsn(INVOKEVIRTUAL, "com/devexperts/dxlab/lincheck/util/Result", "setException", "(Ljava/lang/Exception;)V", false);
            mv.visitLabel(l6);
            mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            mv.visitInsn(RETURN);
            mv.visitMaxs(4, 4);
            mv.visitEnd();
        }
        cw.visitEnd();

        return cw.toByteArray();
    }
}
