package com.devexperts.dxlab.lincheck.transformers;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

/**
 * Inserts Thread.yield method before shared variable
 */
public class ThreadYieldClassVisitor extends BeforeSharedVariableClassVisitor {

    public ThreadYieldClassVisitor(ClassVisitor cv) {
        super(cv);
    }

    @Override
    protected MethodVisitor visitStrategy(MethodVisitor mv, int access, String name, String desc, String signature, String[] exceptions) {
        return new ThreadYieldMethodTransformer(api, mv, name, access, desc);
    }


}
