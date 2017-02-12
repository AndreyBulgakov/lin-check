package com.devexperts.dxlab.lincheck.transformers;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.GeneratorAdapter;

import static org.objectweb.asm.Opcodes.ACC_NATIVE;
import static org.objectweb.asm.Opcodes.ASM5;

/**
 * Inserts consumeCPU method call
 */
public class ConsumeCPUClassVisitor extends BeforeSharedVariableClassVisitor {

    private String className;

    public ConsumeCPUClassVisitor(ClassVisitor cv) {
        super(cv);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        this.className = name;
        super.visit(version, access, name, signature, superName, interfaces);
    }

    @Override
    protected MethodVisitor visitStrategy(MethodVisitor mv, int access, String name, String desc, String signature, String[] exceptions) {
        return new ConsumeCPUMethodTransformer(api, mv, className, name, access, desc);
    }
}
