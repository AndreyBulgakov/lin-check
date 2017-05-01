package com.devexperts.dxlab.lincheck.utils;

/*
 * #%L
 * core
 * %%
 * Copyright (C) 2015 - 2017 Devexperts, LLC
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;


public class InvokeMethodCounter extends ClassVisitor{
    private int count = 0;
    private final String methodName;


    public InvokeMethodCounter(String methodName) {
        super(Opcodes.ASM5);
        this.methodName = methodName;
    }

    public InvokeMethodCounter(ClassVisitor cv, String methodName) {
        super(Opcodes.ASM5, cv);
        this.methodName = methodName;
    }

    public int getCount() {
        return count;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor outMV =  super.visitMethod(access, name, desc, signature, exceptions);
        return new MethodVisitor(Opcodes.ASM5, outMV) {
            String methoName = methodName;
            @Override
            public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
                if (opcode == Opcodes.INVOKESTATIC && name.equals(methodName)){
                    count++;
                }
                super.visitMethodInsn(opcode, owner, name, desc, itf);
            }
        };
    }
}
