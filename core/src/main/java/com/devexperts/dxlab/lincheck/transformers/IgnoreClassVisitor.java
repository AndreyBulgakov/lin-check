package com.devexperts.dxlab.lincheck.transformers;

import com.devexperts.dxlab.lincheck.Utils;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * Ignore class with given name
 */
public class IgnoreClassVisitor extends ClassVisitor{

    private final String ignoreClass;
    private final ClassVisitor jumpCV;

    public IgnoreClassVisitor(ClassVisitor cv, ClassVisitor jumpToCV, String ignoreClass) {
        super(Utils.ASM_VERSION, cv);
        this.ignoreClass = ignoreClass.replace(".", "/");
        this.jumpCV = jumpToCV;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        if (name.equals(ignoreClass)) {
//            System.out.println("Ignored: " + name);
            cv = jumpCV;
            super.visit(version, access, name, signature, superName, interfaces);
        }
        else {
//            System.out.println("Not ignored: " + name);
            cv.visit(version, access, name, signature, superName, interfaces);
        }
    }

}
