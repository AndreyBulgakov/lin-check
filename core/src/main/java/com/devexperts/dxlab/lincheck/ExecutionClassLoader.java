package com.devexperts.dxlab.lincheck;

import com.devexperts.dxlab.lincheck.transformers.BeforeSharedVariableClassVisitor;
import com.devexperts.dxlab.lincheck.transformers.IgnoreClassVisitor;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Loader to load and transform classes.
 * Can delegate some classes to parent ClassLoader.
 */
class ExecutionClassLoader extends ClassLoader {
    private final Map<String, Class<?>> cash = new ConcurrentHashMap<>();
    private final Map<String, byte[]> resources = new ConcurrentHashMap<>();
    private final String testClassName;


    ExecutionClassLoader() {
        testClassName = "";
    }

    ExecutionClassLoader(String testClassName) {
        this.testClassName = testClassName;
    }

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
        // Load transformed class from cash if it exists

        // Print loading class
        // System.out.println("Loading: " + name);
        Class result = cash.get(name);
        if (result != null) {
            return result;
        }

        // Secure some packages
        if (shouldIgnoreClass(name)) {
            // Print delegated class
            // System.out.println("Loaded by super:" + name);
            return super.loadClass(name);
        }

        //Transform and save class
        try {
            // Print transforming class
            // System.out.println("Loaded by exec:" + name);
            ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
            ClassVisitor cv = new BeforeSharedVariableClassVisitor(cw, this);
            ClassVisitor cv0 = new IgnoreClassVisitor(cv, cw, testClassName);
            ClassReader cr = new ClassReader(name);

            cr.accept(cv0, ClassReader.SKIP_FRAMES);

            byte[] resultBytecode = cw.toByteArray();
            result = defineClass(name, resultBytecode, 0, resultBytecode.length);

            resources.put(name, resultBytecode);
            cash.put(name, result);
            return result;
        } catch (SecurityException e) {
            return super.loadClass(name);
        } catch (IOException e) {
            throw new ClassNotFoundException(name, e);
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
                                !className.startsWith("com.devexperts.dxlab.lincheck.tests.") ||
                        className.startsWith("sun.") ||
                        className.startsWith("java.") ||
                        // TODO let's transform java.util.concurrent
                        // TODO check if org.junit is still transforming?
                        className.startsWith("org.junit.");
    }

    //TODO insert onSharedVariable while TestThreadExecutionGenerator generate class
    Class<? extends TestThreadExecution> defineTestThreadExecution(String className, byte[] bytecode) {
        return (Class<? extends TestThreadExecution>) super.defineClass(className, bytecode, 0, bytecode.length);
    }

}
