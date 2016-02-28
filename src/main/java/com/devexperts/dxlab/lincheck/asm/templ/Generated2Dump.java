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
public class Generated2Dump implements Opcodes {

    public static byte[] dump (
            String generatedClassName,
            String testFieldName,
            String testClassName,
            String[] methodNames
    ) throws Exception {

        ClassWriter cw = new ClassWriter(0);
        FieldVisitor fv;
        MethodVisitor mv;
        AnnotationVisitor av0;

        cw.visit(52, ACC_PUBLIC + ACC_SUPER, generatedClassName, null, "com/devexperts/dxlab/lincheck/asm/Generated", null);

        {
            fv = cw.visitField(ACC_PUBLIC, testFieldName, "L" + testClassName + ";", null, null);
            fv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKESPECIAL, "com/devexperts/dxlab/lincheck/asm/Generated", "<init>", "()V", false);
            mv.visitInsn(RETURN);
            mv.visitMaxs(1, 1);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC, "<init>", "(L" + testClassName + ";)V", null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKESPECIAL, "com/devexperts/dxlab/lincheck/asm/Generated", "<init>", "()V", false);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitFieldInsn(PUTFIELD, generatedClassName, testFieldName, "L" + testClassName + ";");
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

            mv.visitLabel(l0);
            mv.visitVarInsn(ALOAD, 3);
            mv.visitIntInsn(BIPUSH, 0);
            mv.visitInsn(IALOAD);
            mv.visitMethodInsn(INVOKESTATIC, "com/devexperts/dxlab/lincheck/util/MyRandom", "busyWait", "(I)V", false);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, generatedClassName, testFieldName, "L" + testClassName + ";");
            mv.visitVarInsn(ALOAD, 1);
            mv.visitIntInsn(BIPUSH, 0);
            mv.visitInsn(AALOAD);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitIntInsn(BIPUSH, 0);
            mv.visitInsn(AALOAD);
            mv.visitMethodInsn(INVOKEVIRTUAL, testClassName, methodNames[0], "(Lcom/devexperts/dxlab/lincheck/util/Result;[Ljava/lang/Object;)V", false);
            mv.visitLabel(l1);
            mv.visitJumpInsn(GOTO, l3);
            mv.visitLabel(l2);
            mv.visitFrame(Opcodes.F_SAME1, 0, null, 1, new Object[] {"java/lang/Exception"});
            mv.visitVarInsn(ASTORE, 4);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitIntInsn(BIPUSH, 0);
            mv.visitInsn(AALOAD);
            mv.visitVarInsn(ALOAD, 4);
            mv.visitMethodInsn(INVOKEVIRTUAL, "com/devexperts/dxlab/lincheck/util/Result", "setException", "(Ljava/lang/Exception;)V", false);

            mv.visitLabel(l3);
            mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            mv.visitVarInsn(ALOAD, 3);
            mv.visitIntInsn(SIPUSH, 1);
            mv.visitInsn(IALOAD);
            mv.visitMethodInsn(INVOKESTATIC, "com/devexperts/dxlab/lincheck/util/MyRandom", "busyWait", "(I)V", false);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, generatedClassName, testFieldName, "L" + testClassName + ";");
            mv.visitVarInsn(ALOAD, 1);
            mv.visitIntInsn(SIPUSH, 1);
            mv.visitInsn(AALOAD);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitIntInsn(SIPUSH, 1);
            mv.visitInsn(AALOAD);
            mv.visitMethodInsn(INVOKEVIRTUAL, testClassName, methodNames[1], "(Lcom/devexperts/dxlab/lincheck/util/Result;[Ljava/lang/Object;)V", false);
            mv.visitLabel(l4);
            Label l6 = new Label();
            mv.visitJumpInsn(GOTO, l6);
            mv.visitLabel(l5);
            mv.visitFrame(Opcodes.F_SAME1, 0, null, 1, new Object[] {"java/lang/Exception"});
            mv.visitVarInsn(ASTORE, 4);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitIntInsn(SIPUSH, 1);
            mv.visitInsn(AALOAD);
            mv.visitVarInsn(ALOAD, 4);
            mv.visitMethodInsn(INVOKEVIRTUAL, "com/devexperts/dxlab/lincheck/util/Result", "setException", "(Ljava/lang/Exception;)V", false);
            mv.visitLabel(l6);

            mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            mv.visitInsn(RETURN);
            mv.visitMaxs(4, 5);
            mv.visitEnd();
        }
        cw.visitEnd();

        return cw.toByteArray();
    }
}