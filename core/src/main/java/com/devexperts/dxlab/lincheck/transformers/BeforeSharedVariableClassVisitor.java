package com.devexperts.dxlab.lincheck.transformers;

import com.devexperts.dxlab.lincheck.Utils;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.ACC_NATIVE;
import static org.objectweb.asm.Opcodes.ASM5;

/**
 * ClassVisitor that don't visit ACC_NATIVE and constructor method.
 */
public abstract class BeforeSharedVariableClassVisitor extends ClassVisitor {


    public BeforeSharedVariableClassVisitor(ClassVisitor cv) {
        super(Utils.ASM_VERSION, cv);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        if ((access & ACC_NATIVE) == 0 && (!name.equals("<init>"))) {
            MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
            return visitStrategy(mv, access, name, desc, signature, exceptions);
        }
        return super.visitMethod(access, name, desc, signature, exceptions);
    }

    /**
     * Return implementation of BeforeSharedVariable strategy
     */
    protected abstract MethodVisitor visitStrategy(MethodVisitor mv, int access, String name, String desc, String signature, String[] exceptions);
}
