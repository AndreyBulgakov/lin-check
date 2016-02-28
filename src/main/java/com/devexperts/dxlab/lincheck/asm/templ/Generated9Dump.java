/*
 *  Lincheck - Linearizability checker
 *  Copyright (C) 2015 Devexperts LLC
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.devexperts.dxlab.lincheck.asm.templ;

import jdk.internal.org.objectweb.asm.*;
public class Generated9Dump implements Opcodes {

    public static byte[] dump () throws Exception {

        ClassWriter cw = new ClassWriter(0);
        FieldVisitor fv;
        MethodVisitor mv;
        AnnotationVisitor av0;

        cw.visit(52, ACC_PUBLIC + ACC_SUPER, "com/devexperts/dxlab/lincheck/asmtest/Generated10", null, "com/devexperts/dxlab/lincheck/asmtest/Generated", null);

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
            mv.visitFieldInsn(PUTFIELD, "com/devexperts/dxlab/lincheck/asmtest/Generated10", "queue", "Lcom/devexperts/dxlab/lincheck/tests/custom/QueueTestAnn;");
            mv.visitInsn(RETURN);
            mv.visitMaxs(2, 2);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC, "process", "([Lcom/devexperts/dxlab/lincheck/util/Result;[[Ljava/lang/Object;[I)V", null, null);
            mv.visitCode();
            Label l0 = new Label();
            Label l1 = new Label();
            Label l2 = new Label();
            mv.visitTryCatchBlock(l0, l1, l2, "java/lang/Exception");
            Label l3 = new Label();
            Label l4 = new Label();
            Label l5 = new Label();
            mv.visitTryCatchBlock(l3, l4, l5, "java/lang/Exception");
            Label l6 = new Label();
            Label l7 = new Label();
            Label l8 = new Label();
            mv.visitTryCatchBlock(l6, l7, l8, "java/lang/Exception");
            Label l9 = new Label();
            Label l10 = new Label();
            Label l11 = new Label();
            mv.visitTryCatchBlock(l9, l10, l11, "java/lang/Exception");
            Label l12 = new Label();
            Label l13 = new Label();
            Label l14 = new Label();
            mv.visitTryCatchBlock(l12, l13, l14, "java/lang/Exception");
            Label l15 = new Label();
            Label l16 = new Label();
            Label l17 = new Label();
            mv.visitTryCatchBlock(l15, l16, l17, "java/lang/Exception");
            Label l18 = new Label();
            Label l19 = new Label();
            Label l20 = new Label();
            mv.visitTryCatchBlock(l18, l19, l20, "java/lang/Exception");
            Label l21 = new Label();
            Label l22 = new Label();
            Label l23 = new Label();
            mv.visitTryCatchBlock(l21, l22, l23, "java/lang/Exception");
            Label l24 = new Label();
            Label l25 = new Label();
            Label l26 = new Label();
            mv.visitTryCatchBlock(l24, l25, l26, "java/lang/Exception");
            mv.visitLabel(l0);
            mv.visitVarInsn(ALOAD, 3);
            mv.visitIntInsn(BIPUSH, 111);
            mv.visitInsn(IALOAD);
            mv.visitMethodInsn(INVOKESTATIC, "com/devexperts/dxlab/lincheck/util/MyRandom", "busyWait", "(I)V", false);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, "com/devexperts/dxlab/lincheck/asmtest/Generated10", "queue", "Lcom/devexperts/dxlab/lincheck/tests/custom/QueueTestAnn;");
            mv.visitVarInsn(ALOAD, 1);
            mv.visitIntInsn(BIPUSH, 111);
            mv.visitInsn(AALOAD);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitIntInsn(BIPUSH, 111);
            mv.visitInsn(AALOAD);
            mv.visitMethodInsn(INVOKEVIRTUAL, "com/devexperts/dxlab/lincheck/tests/custom/QueueTestAnn", "put", "(Lcom/devexperts/dxlab/lincheck/util/Result;[Ljava/lang/Object;)V", false);
            mv.visitLabel(l1);
            mv.visitJumpInsn(GOTO, l3);
            mv.visitLabel(l2);
            mv.visitFrame(Opcodes.F_SAME1, 0, null, 1, new Object[] {"java/lang/Exception"});
            mv.visitVarInsn(ASTORE, 4);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitIntInsn(BIPUSH, 111);
            mv.visitInsn(AALOAD);
            mv.visitVarInsn(ALOAD, 4);
            mv.visitMethodInsn(INVOKEVIRTUAL, "com/devexperts/dxlab/lincheck/util/Result", "setException", "(Ljava/lang/Exception;)V", false);
            mv.visitLabel(l3);
            mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            mv.visitVarInsn(ALOAD, 3);
            mv.visitIntInsn(SIPUSH, 222);
            mv.visitInsn(IALOAD);
            mv.visitMethodInsn(INVOKESTATIC, "com/devexperts/dxlab/lincheck/util/MyRandom", "busyWait", "(I)V", false);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, "com/devexperts/dxlab/lincheck/asmtest/Generated10", "queue", "Lcom/devexperts/dxlab/lincheck/tests/custom/QueueTestAnn;");
            mv.visitVarInsn(ALOAD, 1);
            mv.visitIntInsn(SIPUSH, 222);
            mv.visitInsn(AALOAD);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitIntInsn(SIPUSH, 222);
            mv.visitInsn(AALOAD);
            mv.visitMethodInsn(INVOKEVIRTUAL, "com/devexperts/dxlab/lincheck/tests/custom/QueueTestAnn", "put", "(Lcom/devexperts/dxlab/lincheck/util/Result;[Ljava/lang/Object;)V", false);
            mv.visitLabel(l4);
            mv.visitJumpInsn(GOTO, l6);
            mv.visitLabel(l5);
            mv.visitFrame(Opcodes.F_SAME1, 0, null, 1, new Object[] {"java/lang/Exception"});
            mv.visitVarInsn(ASTORE, 4);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitIntInsn(SIPUSH, 222);
            mv.visitInsn(AALOAD);
            mv.visitVarInsn(ALOAD, 4);
            mv.visitMethodInsn(INVOKEVIRTUAL, "com/devexperts/dxlab/lincheck/util/Result", "setException", "(Ljava/lang/Exception;)V", false);
            mv.visitLabel(l6);
            mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            mv.visitVarInsn(ALOAD, 3);
            mv.visitIntInsn(SIPUSH, 333);
            mv.visitInsn(IALOAD);
            mv.visitMethodInsn(INVOKESTATIC, "com/devexperts/dxlab/lincheck/util/MyRandom", "busyWait", "(I)V", false);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, "com/devexperts/dxlab/lincheck/asmtest/Generated10", "queue", "Lcom/devexperts/dxlab/lincheck/tests/custom/QueueTestAnn;");
            mv.visitVarInsn(ALOAD, 1);
            mv.visitIntInsn(SIPUSH, 333);
            mv.visitInsn(AALOAD);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitIntInsn(SIPUSH, 333);
            mv.visitInsn(AALOAD);
            mv.visitMethodInsn(INVOKEVIRTUAL, "com/devexperts/dxlab/lincheck/tests/custom/QueueTestAnn", "put", "(Lcom/devexperts/dxlab/lincheck/util/Result;[Ljava/lang/Object;)V", false);
            mv.visitLabel(l7);
            mv.visitJumpInsn(GOTO, l9);
            mv.visitLabel(l8);
            mv.visitFrame(Opcodes.F_SAME1, 0, null, 1, new Object[] {"java/lang/Exception"});
            mv.visitVarInsn(ASTORE, 4);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitIntInsn(SIPUSH, 333);
            mv.visitInsn(AALOAD);
            mv.visitVarInsn(ALOAD, 4);
            mv.visitMethodInsn(INVOKEVIRTUAL, "com/devexperts/dxlab/lincheck/util/Result", "setException", "(Ljava/lang/Exception;)V", false);
            mv.visitLabel(l9);
            mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            mv.visitVarInsn(ALOAD, 3);
            mv.visitIntInsn(SIPUSH, 444);
            mv.visitInsn(IALOAD);
            mv.visitMethodInsn(INVOKESTATIC, "com/devexperts/dxlab/lincheck/util/MyRandom", "busyWait", "(I)V", false);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, "com/devexperts/dxlab/lincheck/asmtest/Generated10", "queue", "Lcom/devexperts/dxlab/lincheck/tests/custom/QueueTestAnn;");
            mv.visitVarInsn(ALOAD, 1);
            mv.visitIntInsn(SIPUSH, 444);
            mv.visitInsn(AALOAD);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitIntInsn(SIPUSH, 444);
            mv.visitInsn(AALOAD);
            mv.visitMethodInsn(INVOKEVIRTUAL, "com/devexperts/dxlab/lincheck/tests/custom/QueueTestAnn", "put", "(Lcom/devexperts/dxlab/lincheck/util/Result;[Ljava/lang/Object;)V", false);
            mv.visitLabel(l10);
            mv.visitJumpInsn(GOTO, l12);
            mv.visitLabel(l11);
            mv.visitFrame(Opcodes.F_SAME1, 0, null, 1, new Object[] {"java/lang/Exception"});
            mv.visitVarInsn(ASTORE, 4);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitIntInsn(SIPUSH, 444);
            mv.visitInsn(AALOAD);
            mv.visitVarInsn(ALOAD, 4);
            mv.visitMethodInsn(INVOKEVIRTUAL, "com/devexperts/dxlab/lincheck/util/Result", "setException", "(Ljava/lang/Exception;)V", false);
            mv.visitLabel(l12);
            mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            mv.visitVarInsn(ALOAD, 3);
            mv.visitIntInsn(SIPUSH, 555);
            mv.visitInsn(IALOAD);
            mv.visitMethodInsn(INVOKESTATIC, "com/devexperts/dxlab/lincheck/util/MyRandom", "busyWait", "(I)V", false);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, "com/devexperts/dxlab/lincheck/asmtest/Generated10", "queue", "Lcom/devexperts/dxlab/lincheck/tests/custom/QueueTestAnn;");
            mv.visitVarInsn(ALOAD, 1);
            mv.visitIntInsn(SIPUSH, 555);
            mv.visitInsn(AALOAD);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitIntInsn(SIPUSH, 555);
            mv.visitInsn(AALOAD);
            mv.visitMethodInsn(INVOKEVIRTUAL, "com/devexperts/dxlab/lincheck/tests/custom/QueueTestAnn", "put", "(Lcom/devexperts/dxlab/lincheck/util/Result;[Ljava/lang/Object;)V", false);
            mv.visitLabel(l13);
            mv.visitJumpInsn(GOTO, l15);
            mv.visitLabel(l14);
            mv.visitFrame(Opcodes.F_SAME1, 0, null, 1, new Object[] {"java/lang/Exception"});
            mv.visitVarInsn(ASTORE, 4);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitIntInsn(SIPUSH, 555);
            mv.visitInsn(AALOAD);
            mv.visitVarInsn(ALOAD, 4);
            mv.visitMethodInsn(INVOKEVIRTUAL, "com/devexperts/dxlab/lincheck/util/Result", "setException", "(Ljava/lang/Exception;)V", false);
            mv.visitLabel(l15);
            mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            mv.visitVarInsn(ALOAD, 3);
            mv.visitIntInsn(SIPUSH, 666);
            mv.visitInsn(IALOAD);
            mv.visitMethodInsn(INVOKESTATIC, "com/devexperts/dxlab/lincheck/util/MyRandom", "busyWait", "(I)V", false);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, "com/devexperts/dxlab/lincheck/asmtest/Generated10", "queue", "Lcom/devexperts/dxlab/lincheck/tests/custom/QueueTestAnn;");
            mv.visitVarInsn(ALOAD, 1);
            mv.visitIntInsn(SIPUSH, 666);
            mv.visitInsn(AALOAD);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitIntInsn(SIPUSH, 666);
            mv.visitInsn(AALOAD);
            mv.visitMethodInsn(INVOKEVIRTUAL, "com/devexperts/dxlab/lincheck/tests/custom/QueueTestAnn", "put", "(Lcom/devexperts/dxlab/lincheck/util/Result;[Ljava/lang/Object;)V", false);
            mv.visitLabel(l16);
            mv.visitJumpInsn(GOTO, l18);
            mv.visitLabel(l17);
            mv.visitFrame(Opcodes.F_SAME1, 0, null, 1, new Object[] {"java/lang/Exception"});
            mv.visitVarInsn(ASTORE, 4);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitIntInsn(SIPUSH, 666);
            mv.visitInsn(AALOAD);
            mv.visitVarInsn(ALOAD, 4);
            mv.visitMethodInsn(INVOKEVIRTUAL, "com/devexperts/dxlab/lincheck/util/Result", "setException", "(Ljava/lang/Exception;)V", false);
            mv.visitLabel(l18);
            mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            mv.visitVarInsn(ALOAD, 3);
            mv.visitIntInsn(SIPUSH, 777);
            mv.visitInsn(IALOAD);
            mv.visitMethodInsn(INVOKESTATIC, "com/devexperts/dxlab/lincheck/util/MyRandom", "busyWait", "(I)V", false);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, "com/devexperts/dxlab/lincheck/asmtest/Generated10", "queue", "Lcom/devexperts/dxlab/lincheck/tests/custom/QueueTestAnn;");
            mv.visitVarInsn(ALOAD, 1);
            mv.visitIntInsn(SIPUSH, 777);
            mv.visitInsn(AALOAD);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitIntInsn(SIPUSH, 777);
            mv.visitInsn(AALOAD);
            mv.visitMethodInsn(INVOKEVIRTUAL, "com/devexperts/dxlab/lincheck/tests/custom/QueueTestAnn", "put", "(Lcom/devexperts/dxlab/lincheck/util/Result;[Ljava/lang/Object;)V", false);
            mv.visitLabel(l19);
            mv.visitJumpInsn(GOTO, l21);
            mv.visitLabel(l20);
            mv.visitFrame(Opcodes.F_SAME1, 0, null, 1, new Object[] {"java/lang/Exception"});
            mv.visitVarInsn(ASTORE, 4);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitIntInsn(SIPUSH, 777);
            mv.visitInsn(AALOAD);
            mv.visitVarInsn(ALOAD, 4);
            mv.visitMethodInsn(INVOKEVIRTUAL, "com/devexperts/dxlab/lincheck/util/Result", "setException", "(Ljava/lang/Exception;)V", false);
            mv.visitLabel(l21);
            mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            mv.visitVarInsn(ALOAD, 3);
            mv.visitIntInsn(SIPUSH, 888);
            mv.visitInsn(IALOAD);
            mv.visitMethodInsn(INVOKESTATIC, "com/devexperts/dxlab/lincheck/util/MyRandom", "busyWait", "(I)V", false);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, "com/devexperts/dxlab/lincheck/asmtest/Generated10", "queue", "Lcom/devexperts/dxlab/lincheck/tests/custom/QueueTestAnn;");
            mv.visitVarInsn(ALOAD, 1);
            mv.visitIntInsn(SIPUSH, 888);
            mv.visitInsn(AALOAD);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitIntInsn(SIPUSH, 888);
            mv.visitInsn(AALOAD);
            mv.visitMethodInsn(INVOKEVIRTUAL, "com/devexperts/dxlab/lincheck/tests/custom/QueueTestAnn", "put", "(Lcom/devexperts/dxlab/lincheck/util/Result;[Ljava/lang/Object;)V", false);
            mv.visitLabel(l22);
            mv.visitJumpInsn(GOTO, l24);
            mv.visitLabel(l23);
            mv.visitFrame(Opcodes.F_SAME1, 0, null, 1, new Object[] {"java/lang/Exception"});
            mv.visitVarInsn(ASTORE, 4);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitIntInsn(SIPUSH, 888);
            mv.visitInsn(AALOAD);
            mv.visitVarInsn(ALOAD, 4);
            mv.visitMethodInsn(INVOKEVIRTUAL, "com/devexperts/dxlab/lincheck/util/Result", "setException", "(Ljava/lang/Exception;)V", false);
            mv.visitLabel(l24);
            mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            mv.visitVarInsn(ALOAD, 3);
            mv.visitIntInsn(SIPUSH, 999);
            mv.visitInsn(IALOAD);
            mv.visitMethodInsn(INVOKESTATIC, "com/devexperts/dxlab/lincheck/util/MyRandom", "busyWait", "(I)V", false);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, "com/devexperts/dxlab/lincheck/asmtest/Generated10", "queue", "Lcom/devexperts/dxlab/lincheck/tests/custom/QueueTestAnn;");
            mv.visitVarInsn(ALOAD, 1);
            mv.visitIntInsn(SIPUSH, 999);
            mv.visitInsn(AALOAD);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitIntInsn(SIPUSH, 999);
            mv.visitInsn(AALOAD);
            mv.visitMethodInsn(INVOKEVIRTUAL, "com/devexperts/dxlab/lincheck/tests/custom/QueueTestAnn", "put", "(Lcom/devexperts/dxlab/lincheck/util/Result;[Ljava/lang/Object;)V", false);
            mv.visitLabel(l25);
            Label l27 = new Label();
            mv.visitJumpInsn(GOTO, l27);
            mv.visitLabel(l26);
            mv.visitFrame(Opcodes.F_SAME1, 0, null, 1, new Object[] {"java/lang/Exception"});
            mv.visitVarInsn(ASTORE, 4);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitIntInsn(SIPUSH, 999);
            mv.visitInsn(AALOAD);
            mv.visitVarInsn(ALOAD, 4);
            mv.visitMethodInsn(INVOKEVIRTUAL, "com/devexperts/dxlab/lincheck/util/Result", "setException", "(Ljava/lang/Exception;)V", false);
            mv.visitLabel(l27);
            mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            mv.visitInsn(RETURN);
            mv.visitMaxs(4, 5);
            mv.visitEnd();
        }
        cw.visitEnd();

        return cw.toByteArray();
    }
}
