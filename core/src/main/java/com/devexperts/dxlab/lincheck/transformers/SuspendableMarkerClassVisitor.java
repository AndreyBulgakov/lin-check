package com.devexperts.dxlab.lincheck.transformers;

import com.devexperts.dxlab.lincheck.Utils;
import com.devexperts.dxlab.lincheck.annotations.Reset;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.MethodNode;

import java.lang.reflect.Modifier;
import java.util.HashSet;

/**
 * Created by andrey on 5/4/17.
 */
public class SuspendableMarkerClassVisitor extends ClassVisitor {

    public SuspendableMarkerClassVisitor(ClassVisitor cv) {
        super(Utils.ASM_VERSION, cv);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        if (!Modifier.isNative(access) && name.charAt(0) != '<'){
            MethodVisitor outMV =  super.visitMethod(access, name, desc, signature, exceptions);

            //Mark method as @Suspendable
            AnnotationVisitor insertSuspenable = outMV.visitAnnotation("Lco/paralleluniverse/fibers/Suspendable;",true);
            insertSuspenable.visitEnd();

            //Skip other @Suspendables
            //May be allow @Suspendable repeat? (nedd quasar patch)
            return new MethodVisitor(Utils.ASM_VERSION, outMV) {
                private  boolean isSuspendable = false;
                @Override
                public AnnotationVisitor visitAnnotation(String desc1, boolean visible) {
                if (desc1.equals("Lco/paralleluniverse/fibers/Suspendable;")) {
                    if (isSuspendable){
                        super.visitAnnotation("", false);
                    }
                    else {
                        isSuspendable = true;
                        super.visitAnnotation(desc1, visible);
                    }
                }
                    return super.visitAnnotation(desc1, visible);
                }
            };
        }
        return super.visitMethod(access, name, desc, signature, exceptions);
    }

}
