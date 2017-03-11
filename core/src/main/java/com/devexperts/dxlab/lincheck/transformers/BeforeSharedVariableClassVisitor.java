package com.devexperts.dxlab.lincheck.transformers;

import com.devexperts.dxlab.lincheck.Utils;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.objectweb.asm.Opcodes.ACC_NATIVE;

/**
 * TODO public?
 * ClassVisitor that inserts StrategyHolder before each access to shared variable.
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
        super.visit(version, access, name, signature, superName, interfaces);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        // TODO why constructor shouldn't be transformed? -- Question is still open
//        if (!Modifier.isNative(access) && !name.equals("<init>")) {
        if (!Modifier.isNative(access)) {
            MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
            return new BeforeSharedVariableMethodTransformer(api, mv, access, name, desc, className, loader);
        }
        return super.visitMethod(access, name, desc, signature, exceptions);
    }
}
