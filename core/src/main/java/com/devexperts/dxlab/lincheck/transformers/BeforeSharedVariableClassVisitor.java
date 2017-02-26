package com.devexperts.dxlab.lincheck.transformers;

import com.devexperts.dxlab.lincheck.Utils;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.ACC_NATIVE;

/**
 * ClassVisitor that don't visit ACC_NATIVE and constructor method.
 */
public class BeforeSharedVariableClassVisitor extends ClassVisitor {

    private String className;
    private ClassLoader loader;

    public BeforeSharedVariableClassVisitor(ClassVisitor cv, ClassLoader loader) {
        super(Utils.ASM_VERSION, cv);
        this.loader = loader;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        this.className = name;
//        System.out.println("ConsumeCPU className: " + name);
        super.visit(version, access, name, signature, superName, interfaces);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        if ((access & ACC_NATIVE) == 0 && (!name.equals("<init>"))) {
            MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
            return new BeforeSharedVariableMethodTransformer(api, mv, access, name, desc, className, loader);
        }
        return super.visitMethod(access, name, desc, signature, exceptions);
    }
}
