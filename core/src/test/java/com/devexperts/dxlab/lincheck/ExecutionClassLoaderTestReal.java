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

import com.devexperts.dxlab.lincheck.utils.FakeTestAbstract;
import com.devexperts.dxlab.lincheck.utils.InvokeMethodCounter;
import com.devexperts.dxlab.transformigclasses.FakeTestClass;
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
        cl = new ExecutionClassLoader(FakeTestClass.class.getCanonicalName());
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
