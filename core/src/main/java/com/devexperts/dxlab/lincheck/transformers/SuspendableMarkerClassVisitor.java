package com.devexperts.dxlab.lincheck.transformers;

/*
 * #%L
 * core
 * %%
 * Copyright (C) 2015 - 2017 Devexperts, LLC
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

import co.paralleluniverse.fibers.instrument.MethodDatabase;
import co.paralleluniverse.fibers.instrument.Retransform;
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


public class SuspendableMarkerClassVisitor extends ClassVisitor {

    private String className;
    public SuspendableMarkerClassVisitor(ClassVisitor cv, ClassLoader loader) {
        super(Utils.ASM_VERSION, cv);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        this.className= name;
        super.visit(version, access, name, signature, superName, interfaces);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
//        if (!Modifier.isNative(access) && name.charAt(0) != '<'){
        if (!Modifier.isNative(access) && !name.startsWith("<") && !name.startsWith("access")) {

            MethodVisitor outMV =  super.visitMethod(access, name, desc, signature, exceptions);

            //Mark method as @Suspendable
            AnnotationVisitor insertSuspenable = outMV.visitAnnotation("Lco/paralleluniverse/fibers/Suspendable;",true);
            insertSuspenable.visitEnd();

            //Skip other @Suspendables
            //May be allow @Suspendable repeat? (need Quasar patch)
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
