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

import com.devexperts.dxlab.lincheck.Utils;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

import java.lang.reflect.Modifier;

/**
 * ClassVisitor to transform methods using BeforeSharedVariableMethodTransformer
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
        // TODO We shouldn't ignore all constructors !!!
//        if (!Modifier.isNative(access) && !name.equals("<init>")) {

        if (!Modifier.isNative(access) && !name.startsWith("<")) {
            MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
            MethodVisitor mv2 = new BeforeSharedVariableMethodTransformer(api, mv, access, name, desc, className);
            if (Modifier.isAbstract(access)) {
                AnnotationVisitor av0 = mv2.visitAnnotation("Lco/paralleluniverse/fibers/Suspendable;", true);
                av0.visitEnd();
            }
            return mv2;
//            return new BeforeSharedVariableMethodTransformer(api, mv, access, name, desc, className);
        }
        return super.visitMethod(access, name, desc, signature, exceptions);
    }
}
