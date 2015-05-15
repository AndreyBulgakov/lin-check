package com.devexperts.dxlab.lincheck.asmtest;

import java.util.*;
import org.objectweb.asm.*;
public class Generated1Dump implements Opcodes {

    public static byte[] dump () throws Exception {

        ClassWriter cw = new ClassWriter(0);
        FieldVisitor fv;
        MethodVisitor mv;
        AnnotationVisitor av0;

        cw.visit(52, ACC_PUBLIC + ACC_SUPER, "com/devexperts/dxlab/lincheck/asmtest/Generated1", null, "com/devexperts/dxlab/lincheck/asmtest/Generated", null);

        {
            fv = cw.visitField(ACC_PUBLIC, "queue", "Lcom/devexperts/dxlab/lincheck/tests/custom/QueueTestAnn;", null, null);
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
            mv = cw.visitMethod(ACC_PUBLIC, "<init>", "(Lcom/devexperts/dxlab/lincheck/tests/custom/QueueTestAnn;)V", null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKESPECIAL, "com/devexperts/dxlab/lincheck/asmtest/Generated", "<init>", "()V", false);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitFieldInsn(PUTFIELD, "com/devexperts/dxlab/lincheck/asmtest/Generated1", "queue", "Lcom/devexperts/dxlab/lincheck/tests/custom/QueueTestAnn;");
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
            mv.visitFieldInsn(GETFIELD, "com/devexperts/dxlab/lincheck/asmtest/Generated1", "queue", "Lcom/devexperts/dxlab/lincheck/tests/custom/QueueTestAnn;");
            mv.visitVarInsn(ALOAD, 1);
            mv.visitIntInsn(BIPUSH, 123);
            mv.visitInsn(AALOAD);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitIntInsn(BIPUSH, 123);
            mv.visitInsn(AALOAD);
            mv.visitMethodInsn(INVOKEVIRTUAL, "com/devexperts/dxlab/lincheck/tests/custom/QueueTestAnn", "put", "(Lcom/devexperts/dxlab/lincheck/util/Result;[Ljava/lang/Object;)V", false);
            mv.visitLabel(l1);
            mv.visitJumpInsn(GOTO, l3);
            mv.visitLabel(l2);
            mv.visitFrame(Opcodes.F_SAME1, 0, null, 1, new Object[] {"java/lang/Exception"});
            mv.visitVarInsn(ASTORE, 3);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitIntInsn(BIPUSH, 123);
            mv.visitInsn(AALOAD);
            mv.visitVarInsn(ALOAD, 3);
            mv.visitMethodInsn(INVOKEVIRTUAL, "com/devexperts/dxlab/lincheck/util/Result", "setException", "(Ljava/lang/Exception;)V", false);
            mv.visitLabel(l3);
            mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, "com/devexperts/dxlab/lincheck/asmtest/Generated1", "queue", "Lcom/devexperts/dxlab/lincheck/tests/custom/QueueTestAnn;");
            mv.visitVarInsn(ALOAD, 1);
            mv.visitIntInsn(SIPUSH, 456);
            mv.visitInsn(AALOAD);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitIntInsn(SIPUSH, 456);
            mv.visitInsn(AALOAD);
            mv.visitMethodInsn(INVOKEVIRTUAL, "com/devexperts/dxlab/lincheck/tests/custom/QueueTestAnn", "get", "(Lcom/devexperts/dxlab/lincheck/util/Result;[Ljava/lang/Object;)V", false);
            mv.visitLabel(l4);
            Label l6 = new Label();
            mv.visitJumpInsn(GOTO, l6);
            mv.visitLabel(l5);
            mv.visitFrame(Opcodes.F_SAME1, 0, null, 1, new Object[] {"java/lang/Exception"});
            mv.visitVarInsn(ASTORE, 3);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitIntInsn(SIPUSH, 456);
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