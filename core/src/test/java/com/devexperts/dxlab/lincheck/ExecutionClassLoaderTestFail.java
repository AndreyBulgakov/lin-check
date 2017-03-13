package com.devexperts.dxlab.lincheck;


import com.devexperts.dxlab.lincheck.utils.AParrent;
import com.devexperts.dxlab.lincheck.utils.InvokeMethodCounter;
import com.devexperts.dxlab.transformigclasses.A;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.objectweb.asm.ClassReader;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;

//TODO I dont know how to solve LinkageError (Example in testMultipleLoaders)
// Run each test separated from others

public class ExecutionClassLoaderTestFail {
    ExecutionClassLoader cl;

    @Before
    public void initialize() throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        cl = new ExecutionClassLoader("");
    }
    @Test
    public void testMultipleLoaders() throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        ExecutionClassLoader cl1 = new ExecutionClassLoader("");
        Class<? extends AParrent> aClass = (Class<? extends AParrent>) cl1.loadClass(A.class.getCanonicalName());
        AParrent aParrent1 = aClass.newInstance();

        ExecutionClassLoader cl2 = new ExecutionClassLoader("");
        Class<? extends AParrent> aClass2 = (Class<? extends AParrent>) cl2.loadClass(A.class.getCanonicalName());
        AParrent aParrent2 = aClass2.newInstance();

//        cl = new ExecutionClassLoader();
        ClassLoader bClassLoader1 = aParrent1.getB().getClass().getClassLoader();
        ClassLoader bClassLoader2 = aParrent2.getB().getClass().getClassLoader();
        Assert.assertNotEquals(bClassLoader1, bClassLoader2);
    }

    @Test(expected = NullPointerException.class)
    public void testNullLoadClass() throws ClassNotFoundException {
        Class aClass = cl.loadClass(null);
    }

    @Test
    public void testSimpleLoadClass() throws ClassNotFoundException {
        Class aClass = cl.loadClass(A.class.getCanonicalName());
        ClassLoader aClassLoader = aClass.getClassLoader();
        Assert.assertEquals(cl, aClassLoader);
    }

    @Test
    public void testNestedLoadClass() throws ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        Class<? extends AParrent> aClass = (Class<? extends AParrent>) cl.loadClass(A.class.getCanonicalName());
        AParrent aParrent1 = aClass.newInstance();
        ClassLoader bClassLoader = aParrent1.getB().getClass().getClassLoader();
        Assert.assertEquals(cl, bClassLoader);
    }

    @Test
    public void testSecurityLoadClass() throws ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException     {
        Class<? extends AParrent> aClass = (Class<? extends AParrent>) cl.loadClass(A.class.getCanonicalName());
        AParrent aParrent1 = aClass.newInstance();
        ClassLoader bClassLoader = aParrent1.getSecutityString().getClass().getClassLoader();
        Assert.assertNotEquals(cl, bClassLoader);
    }

    @Test
    public void testIgnoreLoadClass() throws ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException, IOException {
        String aName = A.class.getCanonicalName();

        ClassReader cleanCrCounter = new ClassReader(A.class.getCanonicalName());
        InvokeMethodCounter cleanYieldCounter = new InvokeMethodCounter("yield");
        InvokeMethodCounter cleanCPUCounter = new InvokeMethodCounter(cleanYieldCounter,"consumeCPU");
        cleanCrCounter.accept(cleanCPUCounter, ClassReader.SKIP_FRAMES);


        Class<? extends AParrent> aClass = (Class<? extends AParrent>) cl.loadClass(aName);
        AParrent aParrent1 = aClass.newInstance();
        ClassLoader aClassLoader = aParrent1.getClass().getClassLoader();

        InputStream transformedStream = cl.getResourceAsStream(A.class.getCanonicalName());
        ClassReader crCounter = new ClassReader(transformedStream);
        InvokeMethodCounter yieldCounter = new InvokeMethodCounter("yield");
        InvokeMethodCounter CPUCounter = new InvokeMethodCounter(yieldCounter,"consumeCPU");
        crCounter.accept(CPUCounter, ClassReader.SKIP_FRAMES);


//        Assert.assertEquals(aParrent1.getB().getClass().getClassLoader(), cl);
        Assert.assertEquals(cleanYieldCounter.getCount() + cleanCPUCounter.getCount(),
                yieldCounter.getCount() + CPUCounter.getCount());
    }
}
