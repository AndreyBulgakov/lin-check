package com.devexperts.dxlab.lincheck.utils;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * Created by andrey on 2/19/17.
 */
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
