package com.devexperts.dxlab.lincheck;

import com.devexperts.dxlab.lincheck.utils.AParrent;
import com.devexperts.dxlab.lincheck.utils.FakeTestAbstract;
import com.devexperts.dxlab.transformigclasses.FakeTestClass;
import com.devexperts.dxlab.lincheck.utils.InvokeMethodCounter;
import com.devexperts.dxlab.transformigclasses.A;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.objectweb.asm.ClassReader;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;


public class ExecutionClassLoaderTestReal {
    static ExecutionClassLoader cl;
    static FakeTestAbstract fakeTestClass;

    @BeforeClass
    public static void initialize() throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        cl = new ExecutionClassLoader();
        cl.setTestClassName(FakeTestClass.class.getCanonicalName());
        Class<? extends FakeTestAbstract> fake = (Class<? extends FakeTestAbstract>)
                cl.loadClass(FakeTestClass.class.getCanonicalName());
        fakeTestClass = fake.newInstance();
    }

    @Test(expected = NullPointerException.class)
    public void testNullLoadClass() throws ClassNotFoundException {
        Class aClass = cl.loadClass(null);
    }

    @Test
    public void testSimpleLoadClass() throws ClassNotFoundException {
        ClassLoader aClassLoader = fakeTestClass.getClass().getClassLoader();
        Assert.assertEquals(cl, aClassLoader);
    }

    @Test
    public void testNestedLoadClass() throws ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        ClassLoader aClassLoader = fakeTestClass.getA().getClass().getClassLoader();
        Assert.assertEquals(cl, aClassLoader);
    }

    @Test
    public void testSecurityLoadClass() throws ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException     {
        ClassLoader stringCL = fakeTestClass.getString().getClass().getClassLoader();
        Assert.assertNotEquals(cl, stringCL);
    }

    @Test
    public void testIgnoreLoadClass() throws ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException, IOException {
        String fakeTestName = FakeTestClass.class.getCanonicalName();

        ClassReader cleanCrCounter = new ClassReader(FakeTestClass.class.getCanonicalName());
        InvokeMethodCounter cleanYieldCounter = new InvokeMethodCounter("yield");
        InvokeMethodCounter cleanCPUCounter = new InvokeMethodCounter(cleanYieldCounter,"consumeCPU");
        cleanCrCounter.accept(cleanCPUCounter, ClassReader.SKIP_FRAMES);

        InputStream transformedStream = cl.getResourceAsStream(fakeTestName);
        ClassReader crCounter = new ClassReader(transformedStream);
        InvokeMethodCounter yieldCounter = new InvokeMethodCounter("yield");
        InvokeMethodCounter CPUCounter = new InvokeMethodCounter(yieldCounter,"consumeCPU");
        crCounter.accept(CPUCounter, ClassReader.SKIP_FRAMES);


        Assert.assertEquals(cleanYieldCounter.getCount() + cleanCPUCounter.getCount(),
                yieldCounter.getCount() + CPUCounter.getCount());
    }
}
