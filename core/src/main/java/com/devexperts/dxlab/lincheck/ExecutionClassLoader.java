package com.devexperts.dxlab.lincheck;

import com.devexperts.dxlab.lincheck.transformers.ConsumeCPUClassVisitor;
import com.devexperts.dxlab.lincheck.transformers.IgnoreClassVisitor;
import com.devexperts.dxlab.lincheck.transformers.ThreadYieldClassVisitor;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;


import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Loads and transform classes
 */
public class ExecutionClassLoader extends ClassLoader {

    private final Map<String, Class<?>> cash = new ConcurrentHashMap<>();
    private String testClassName = "";

    private static final ExecutionClassLoader INSTANCE = new ExecutionClassLoader();

    private ExecutionClassLoader() {
    }

    public static ExecutionClassLoader getInstance() {
        return INSTANCE;
    }

    public void setTestClassName(String testClassName) {
        this.testClassName = testClassName;
    }

    /***
     * Transform class if it is not in excluded list and load it by this Loader
     * else delegate load to parent loader
     * @param name name of class
     * @return transformed class loaded by this loader or by parent loader
     * @throws ClassNotFoundException if IOException
     */
    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        // Load transformed class from cash if it exists
        Class result = cash.get(name);
        if (result != null) {
            return result;
        }

        // TODO Excluded list
        // TODO Need resolve?
        // Secure some packages
        if (name != null &&
                (name.startsWith("com.devexperts.dxlab.lincheck") &&
                        !name.startsWith("com.devexperts.dxlab.lincheck.test") ||
                        name.startsWith("sun.") || name.startsWith("java.") || name.startsWith("org.junit."))) {
            return super.loadClass(name);
        }

        //Transform and save class
        try {
//            System.out.println(name);
            ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
            //TODO Strategy choose
            //TODO maybe set all CV chain in Classloader?
            ClassVisitor cv = new ConsumeCPUClassVisitor(cw);
//            ClassVisitor cv = new ThreadYieldClassVisitor(cw);
            ClassVisitor cv0 = new IgnoreClassVisitor(cv, testClassName);
            ClassReader cr = new ClassReader(name);

            cr.accept(cv0, ClassReader.EXPAND_FRAMES);
            byte[] resultBytecode = cw.toByteArray();
            result = defineClass(name, resultBytecode, 0, resultBytecode.length);

            cash.put(name, result);
            return result;
        } catch (SecurityException e) {
            return super.loadClass(name);
        } catch (IOException e) {
            throw new ClassNotFoundException(name, e);
        }
    }

    public Class<? extends TestThreadExecution> define(String className, byte[] bytecode) {
        return (Class<? extends TestThreadExecution>) super.defineClass(className, bytecode, 0, bytecode.length);
    }

}
