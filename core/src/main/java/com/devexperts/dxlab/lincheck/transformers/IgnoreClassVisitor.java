package com.devexperts.dxlab.lincheck.transformers;

import com.devexperts.dxlab.lincheck.Utils;
import org.objectweb.asm.ClassVisitor;

/**
 * Ignore class with given name
 */
public class IgnoreClassVisitor extends ClassVisitor{

    private final String ignoreClass;

    public IgnoreClassVisitor(ClassVisitor cv, String ignoreClass) {
        super(Utils.ASM_VERSION, cv);
        this.ignoreClass = ignoreClass;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        if (name.equals(ignoreClass)) {
//            System.out.println("Ignored: " + name);
            super.visit(version, access, name, signature, superName, interfaces);
        }
        else {
//            System.out.println("Not ignored: " + name);
            cv.visit(version, access, name, signature, superName, interfaces);
        }
    }

}
