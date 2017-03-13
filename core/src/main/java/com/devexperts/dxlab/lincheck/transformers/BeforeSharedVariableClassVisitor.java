package com.devexperts.dxlab.lincheck.transformers;

import com.devexperts.dxlab.lincheck.Utils;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

import java.lang.reflect.Modifier;

/**
 * ClassVisitor to transform methods by using BeforeSharedVariableMethodTransformer
 */
public class BeforeSharedVariableClassVisitor extends ClassVisitor {

    private String className;

    public BeforeSharedVariableClassVisitor(ClassVisitor cv) {
        super(Utils.ASM_VERSION, cv);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        this.className = name;
        super.visit(version, access, name, signature, superName, interfaces);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        // TODO We shouldn't ignore all constructors
        if (!Modifier.isNative(access) && !name.equals("<init>")) {
            MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
            return new BeforeSharedVariableMethodTransformer(api, mv, access, name, desc, className);
        }
        return super.visitMethod(access, name, desc, signature, exceptions);
    }
}
