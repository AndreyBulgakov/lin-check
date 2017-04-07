package com.devexperts.dxlab.lincheck;

import co.paralleluniverse.fibers.instrument.QuasarInstrumentor;
import co.paralleluniverse.fibers.instrument.Retransform;
import co.paralleluniverse.fibers.instrument.SuspendableHelper;
import com.devexperts.dxlab.lincheck.transformers.BeforeSharedVariableClassVisitor;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.instrument.ClassDefinition;
import java.util.Arrays;
import java.util.Collections;
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

    ExecutionClassLoader(String testClassName) {
        this.testClassName = testClassName;

    }

    ExecutionClassLoader(ClassLoader parent, String testClassName) {
        super(parent);
        this.testClassName = testClassName;
        this.instrumentor.setVerbose(true);
        this.instrumentor.setDebug(true);

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
        // Print loading class
        // System.out.println("Loading: " + name);

        // Load transformed class from cache if it exists
        Class result = cache.get(name);
        if (result != null)
            return result;
        // Do not transform some classes
        if (shouldIgnoreClass(name)) {
            // Print delegated class
            System.out.println("Loaded by super:" + name);
            return super.loadClass(name);
        }
        //Transform and save class
        try {
            // Print transforming class
            System.out.println("Loaded by exec:" + name);
            ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
            ClassVisitor cv = new BeforeSharedVariableClassVisitor(cw);
            ClassReader cr = new ClassReader(name);
            // Ignore TestClass
            // TODO transform test class too. Use DummyStrategy (and write it) during new instance constructing
            if (name.equals(testClassName)) {
                cr.accept(cw, ClassReader.SKIP_FRAMES);
            } else {
                cr.accept(cv, ClassReader.SKIP_FRAMES);
            }
            // Get transformed bytecode
            byte[] resultBytecode = cw.toByteArray();
//            resultBytecode = instrumentor.instrumentClass(this, name, resultBytecode);
//            System.out.println("==========="+Retransform.isWaiver("com.devexperts.dxlab.lincheck.tests.counter.SimpleWrongCounter1", "incrementAndGet"));
            resultBytecode = Retransform.getInstrumentor().instrumentClass(this, name, resultBytecode);
            ClassDefinition definition = new ClassDefinition(Class.forName(name), resultBytecode);
            Retransform.redefine(Collections.singleton(definition));
            result = Class.forName(name);
//            result = defineClass(name, resultBytecode, 0, resultBytecode.length);
            System.err.println("----" + result.getName() + "-----" + SuspendableHelper.isInstrumented(result));
            System.err.println(Arrays.toString(result.getAnnotations()));
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
        Retransform.addWaiver(className, "call");
//        byte[] resultByteCode = Retransform.getInstrumentor().instrumentClass(this, className, bytecode);
//        Class<? extends TestThreadExecution> result = (Class<? extends TestThreadExecution>) super.defineClass(className, resultByteCode, 0, resultByteCode.length);

//        System.out.println(SuspendableHelper.isInstrumented(result));
//        return result;
        return (Class<? extends TestThreadExecution>) super.defineClass(className, bytecode, 0, bytecode.length);
    }

}
