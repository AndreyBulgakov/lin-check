package com.devexperts.dxlab.lincheck;

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

import co.paralleluniverse.fibers.instrument.QuasarInstrumentor;
import co.paralleluniverse.fibers.instrument.Retransform;
import com.devexperts.dxlab.lincheck.transformers.BeforeSharedVariableClassVisitor;
import org.objectweb.asm.*;
import org.objectweb.asm.commons.ClassRemapper;
import org.objectweb.asm.commons.SimpleRemapper;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Loader to load and transform classes.
 * Can delegate some classes to parent ClassLoader.
 */
class ExecutionClassLoader extends ClassLoader {
    private final Map<String, Class<?>> cache = new ConcurrentHashMap<>();
    private final Map<String, byte[]> resources = new ConcurrentHashMap<>();
    private final String testClassName; // TODO we should transform test class (it contains algorithm logic)
    private final QuasarInstrumentor instrumentor = new QuasarInstrumentor(); //TODO is instrumentor must be single?
//    private final QuasarInstrumentor instrumentor = new QuasarInstrumentor(this); //TODO is instrumentor must be single?

    ExecutionClassLoader(String testClassName) {
        this.testClassName = testClassName;

    }

    ExecutionClassLoader(ClassLoader parent, String testClassName) {
        super(parent);
        this.testClassName = testClassName;
        this.instrumentor.setVerbose(true);
        this.instrumentor.setDebug(true);

    }

    public Class<?> loadTestClass(String name) throws ClassNotFoundException {
        // Print loading class
        // System.out.println("Loading: " + name);

        // Load transformed class from cache if it exists
        Class result = cache.get(name);
        if (result != null)
            return result;
        // Do not transform some classes
        if (shouldIgnoreClass(name)) {
            // Print delegated class
//            System.out.println("Loaded by super:" + name);
            return super.loadClass(name);
        }
        //Transform and save class
        try {
            // Print transforming class
//            System.out.println("Loaded by exec:" + name);
            String oldName = name;
            name += "Generated";
            ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
//            ClassVisitor cv = new BeforeSharedVariableClassVisitor(cw);
            ClassVisitor cv = new ClassVisitor(Opcodes.ASM5, cw) {
                @Override
                public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {

                    if (!Modifier.isNative(access) && !name.startsWith("<")) {
                        MethodVisitor outMV = super.visitMethod(access, name, desc, signature, exceptions);
                        return new MethodVisitor(Opcodes.ASM5, outMV) {
                            boolean isSuspendable = false;
                            boolean isReset = false;

                            @Override
                            public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
                                if (desc.equals("Lco/paralleluniverse/fibers/Suspendable;"))
                                    isSuspendable = true;
                                if (desc.equals("Lcom/devexperts/dxlab/lincheck/annotations/Reset;"))
                                    isReset = true;
                                return super.visitAnnotation(desc, visible);
                            }

                            @Override
                            public void visitCode() {
                                if (isSuspendable || isReset) {
                                    super.visitCode();
                                } else {
                                    visitAnnotation("Lco/paralleluniverse/fibers/Suspendable;", true);
                                    super.visitCode();
                                }
                            }
                        };
                    }
                    return super.visitMethod(access, name, desc, signature, exceptions);
                }
            };

            SimpleRemapper rem = new SimpleRemapper(oldName.replace(".", "/"), name.replace(".", "/"));
//            Remapper rem = new SimpleRemapper(map);
            ClassVisitor remapper = new ClassRemapper(cv, rem);
            ClassReader cr = new ClassReader(oldName);
            // Ignore TestClass
            // TODO transform test class too. Use DummyStrategy (and write it) during new instance constructing
            cr.accept(remapper, ClassReader.SKIP_FRAMES);
            // Get transformed bytecode
            byte[] resultBytecode = cw.toByteArray();
            //TODO classes not instrumented by quasar.
            resultBytecode = Retransform.getInstrumentor().instrumentClass(getParent(), name, resultBytecode);
//            resultBytecode = instrumentor.instrumentClass(getParent(), name, resultBytecode);
//            resultBytecode = Retransform.getInstrumentor().instrumentClass(this, name, resultBytecode);
//            resultBytecode = instrumentor.instrumentClass(this, name, resultBytecode);
//            resultBytecode = instrumentor.instrumentClass(name, resultBytecode);
            writeToFile(name, resultBytecode);
            result = defineClass(name, resultBytecode, 0, resultBytecode.length);
            // Save it to cache and resources
            resources.put(name, resultBytecode);
            cache.put(name, result);
            return result;
        } catch (SecurityException e) {
            return super.loadClass(name);
        } catch (IOException e) {
            throw new ClassNotFoundException(name, e); // TODO write more helpful message
        }
    }
    /**
     * Transform class if it is not in excluded list and load it by this Loader
     * else delegate load to parent loader
     *
     * @param name name of class
     * @return transformed class loaded by this loader or by parent loader
     * @throws ClassNotFoundException if IOException
     */
//    private static List<String> needRename = Arrays.asList("amino_cbbs.LockFreeDeque",
//            "amino_cbbs.DequeNode", "amino_cbbs.AnchorType");
    private static Map<String, String> map = new HashMap<>();

    static {
        map.put("amino_cbbs/LockFreeDeque", "amino_cbbs/LockFreeDequeGenerated");
        map.put("amino_cbbs/DequeNode", "amino_cbbs/DequeNodeGenerated");
        map.put("amino_cbbs/AnchorType", "amino_cbbs/AnchorTypeGenerated");
        map.put("com/devexperts/dxlab/lincheck/tests/amino_cbbs/DequeTest", "com/devexperts/dxlab/lincheck/tests/amino_cbbs/DequeTestGenerated");
        map.put("amino_cbbs/LockFreeDeque$DeqIterator", "amino_cbbs/LockFreeDequeGenerated$DeqIteratorGenerated");
    }

    private static Map<String, String> mapRemNorm = new HashMap<>();

    static {
        mapRemNorm.put("amino_cbbs.LockFreeDequeGenerated", "amino_cbbs.LockFreeDeque");
        mapRemNorm.put("amino_cbbs.DequeNodeGenerated", "amino_cbbs.DequeNode");
        mapRemNorm.put("amino_cbbs.AnchorTypeGenerated", "amino_cbbs.AnchorType");
        mapRemNorm.put("com.devexperts.dxlab.lincheck.tests.amino_cbbs.DequeTestGenerated", "com.devexperts.dxlab.lincheck.tests.amino_cbbs.DequeTest");
        mapRemNorm.put("amino_cbbs.LockFreeDequeGenerated$DeqIteratorGenerated", "amino_cbbs.LockFreeDeque$DeqIterator");
    }
    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {

        // Print loading class
//         System.out.println("Loading: " + name);
        // Load transformed class from cache if it exists
        Class result = cache.get(name);
        if (result != null)
            return result;
        // Do not transform some classes
        if (shouldIgnoreClass(name)) {
            // Print delegated class
//            System.out.println("Loaded by super:" + name);
            return super.loadClass(name);
        }
//        name = mapRemNorm.get(name);
//        System.out.println(cache);
        //Transform and save class
        try {
            // Print transforming class
//            System.out.println("Loaded by exec:" + name);
            ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
            ClassVisitor cv = new BeforeSharedVariableClassVisitor(cw);
//            Remapper remapper = new SimpleRemapper(map);
//            ClassVisitor remampCV = new ClassRemapper(cv, remapper);
            ClassReader cr = new ClassReader(name);
            // Ignore TestClass
            // TODO transform test class too. Use DummyStrategy (and write it) during new instance constructing
//            cr.accept(remampCV, ClassReader.SKIP_FRAMES);
            cr.accept(cv, ClassReader.SKIP_FRAMES);
            // Get transformed bytecode
            byte[] resultBytecode = cw.toByteArray();
            //TODO classes not instrumented by quasar.
//            name += "Generated";
//            name = name.replace("$","Generated$");
//            System.out.println(name);
//            name = map.get(name);
            resultBytecode = Retransform.getInstrumentor().instrumentClass(getParent(), name, resultBytecode);
//            resultBytecode = instrumentor.instrumentClass(getParent(), name, resultBytecode);
//            resultBytecode = Retransform.getInstrumentor().instrumentClass(this, name, resultBytecode);
//            resultBytecode = instrumentor.instrumentClass(this, name, resultBytecode);
//            resultBytecode = instrumentor.instrumentClass(name, resultBytecode);
            //writeToFile(name, resultBytecode);
            result = defineClass(name, resultBytecode, 0, resultBytecode.length);
            // Save it to cache and resources
            resources.put(name, resultBytecode);
            cache.put(name, result);
            return result;
        } catch (SecurityException e) {
            return super.loadClass(name);
        } catch (IOException e) {
            throw new ClassNotFoundException(name, e); // TODO write more helpful message
        }
    }

    @Override
    public InputStream getResourceAsStream(String name) {
        byte[] result = resources.get(name);
        if (result != null) {
            return new ByteArrayInputStream(result);
        } else {
            return super.getResourceAsStream(name);
        }
    }

    /***
     * Check if class should be ignored for transforming and defining
     * @param className checking class name
     * @return result of checking class
     */
    private static boolean shouldIgnoreClass(String className) {
        return
                className == null ||
                        className.startsWith("com.devexperts.dxlab.lincheck.") &&
                                !className.startsWith("com.devexperts.dxlab.lincheck.tests.") &&
                                !className.startsWith("com.devexperts.dxlab.lincheck.libtest.")
                        ||
                        className.startsWith("sun.") ||
                        className.startsWith("co.paralleluniverse.") ||
                        className.startsWith("java.");
                        // TODO let's transform java.util.concurrent
    }

    Class<? extends TestThreadExecution> defineTestThreadExecution(String className, byte[] bytecode) {
//        Retransform.addWaiver(className, "call");

        try {
            bytecode = Retransform.getInstrumentor().instrumentClass(getParent(), className, bytecode);
//            bytecode = instrumentor.instrumentClass(getParent(), className, bytecode);
//            bytecode = Retransform.getInstrumentor().instrumentClass(this, className, bytecode);
//            bytecode = instrumentor.instrumentClass(this, className, bytecode);
//            bytecode = instrumentor.instrumentClass(className, bytecode);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //writeToFile(className, bytecode);
        return (Class<? extends TestThreadExecution>) super.defineClass(className, bytecode, 0, bytecode.length);
    }

    void writeToFile(String className, byte[] bytecode) {
//        try {
//            FileOutputStream stream = new FileOutputStream("out/" + className + ".class");
//            stream.write(bytecode);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }
}
