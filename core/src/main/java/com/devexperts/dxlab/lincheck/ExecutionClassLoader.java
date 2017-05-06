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
import com.devexperts.dxlab.lincheck.transformers.BeforeSharedVariableClassVisitor;
import com.devexperts.dxlab.lincheck.transformers.SuspendableMarkerClassVisitor;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Loader to load and transform classes.
 * Can delegate some classes to parent ClassLoader.
 */
//TODO Make loading already transformed bytecode;
class ExecutionClassLoader extends ClassLoader {
    private final Map<String, Class<?>> cache = new ConcurrentHashMap<>();
    private final Map<String, byte[]> resourcesInstrumentedByShared = new ConcurrentHashMap<>();
    private final Map<String, byte[]> resourcesInstrumentedByQuasar = new ConcurrentHashMap<>();
    private final String testClassName; // TODO we should transform test class (it contains algorithm logic)
    private final QuasarInstrumentor instrumentor = new QuasarInstrumentor(); //TODO is instrumentor must be single?

    ExecutionClassLoader(String testClassName) {
        this.testClassName = testClassName;

    }

    ExecutionClassLoader(ClassLoader parent, String testClassName) {
        super(parent);
        this.testClassName = testClassName;
        this.instrumentor.setVerbose(true);
        this.instrumentor.setDebug(true);

    }
    static volatile int c=0;
    /**
     * Transform class if it is not in excluded list and load it by this Loader
     * else delegate load to parent loader
     *
     * @param name name of class
     * @return transformed class loaded by this loader or by parent loader
     * @throws ClassNotFoundException if IOException
     */
    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
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
            // Get transformed bytecode
            byte[] resultBytecode= instrument(name);
            resultBytecode = quasarInstrument(name, resultBytecode);
            c++;
            writeToFile(name+c, resultBytecode);
            result = defineClass(name, resultBytecode, 0, resultBytecode.length);
            // Save it to cache and resourcesInstrumentedByShared
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
        String nameWithoutSlashes = name.replace("/",".");
        String className = nameWithoutSlashes.substring(0, nameWithoutSlashes.length() - 6);
        byte[] result = resourcesInstrumentedByShared.get(className);
        if (result != null){
            return new ByteArrayInputStream(result);
        }
        if (shouldIgnoreClass(className)){
            return super.getResourceAsStream(name);
        }
        try {
            result = instrument(className);
//            result = quasarInstrument(className, result);
            resourcesInstrumentedByShared.put(className, result);
            return new ByteArrayInputStream(result);
        } catch (IOException e) {
            return null;
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
        bytecode = quasarInstrument(className, bytecode);
//        writeToFile(className, bytecode);
        return (Class<? extends TestThreadExecution>) super.defineClass(className, bytecode, 0, bytecode.length);
    }

    private byte[] instrument(String name) throws IOException {
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        ClassVisitor marker = new SuspendableMarkerClassVisitor(cw, this);
        ClassVisitor cv = new BeforeSharedVariableClassVisitor(marker);
        ClassReader cr = new ClassReader(name);
        cr.accept(cv, ClassReader.SKIP_FRAMES);
        // Get transformed bytecode
        return cw.toByteArray();
    }


    private byte[] quasarInstrument(String className, byte[] bytecode) {
        try {
//        bytecode = Retransform.getInstrumentor().instrumentClass(this, className, bytecode);
//        bytecode = instrumentor.instrumentClass(getParent(), className, bytecode);
//        bytecode = instrumentor.instrumentClass(this, className, bytecode);
//        bytecode = instrumentor.instrumentClass(className, bytecode);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return bytecode;
    }

    private void writeToFile(String className, byte[] bytecode) {
        try {
            FileOutputStream stream = new FileOutputStream("out/" + className + ".class");
            stream.write(bytecode);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}