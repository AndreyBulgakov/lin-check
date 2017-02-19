package com.devexperts.dxlab.lincheck;

import com.devexperts.dxlab.lincheck.transformers.ConsumeCPUClassVisitor;
import com.devexperts.dxlab.lincheck.transformers.IgnoreClassVisitor;
import com.devexperts.dxlab.lincheck.transformers.ThreadYieldClassVisitor;
import com.devexperts.dxlab.lincheck.utils.InvokeMethodCounter;
import com.devexperts.dxlab.transformigclasses.A;
import org.junit.Assert;
import org.junit.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

import java.io.IOException;

/**
 * Created by andrey on 2/19/17.
 */
public class TransformationTest {

    @Test
    public void TestConsumeCPU() throws IOException {
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        ClassVisitor cv = new ConsumeCPUClassVisitor(cw);
//            ClassVisitor cv = new ThreadYieldClassVisitor(cw);
//        ClassVisitor cv0 = new IgnoreClassVisitor(cv, testClassName);
        ClassReader cr = new ClassReader(A.class.getCanonicalName());
        cr.accept(cv, ClassReader.EXPAND_FRAMES);
//        cr.accept(cv, ClassReader.SKIP_FRAMES);

        byte[] resultBytecode = cw.toByteArray();

        ClassReader crCount = new ClassReader(resultBytecode);
        InvokeMethodCounter CPUcounter = new InvokeMethodCounter("consumeCPU");
        crCount.accept(CPUcounter, ClassReader.SKIP_FRAMES);

        Assert.assertEquals(10, CPUcounter.getCount());
    }

    @Test
    public void TestThreadYield() throws IOException {
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
            ClassVisitor cv = new ThreadYieldClassVisitor(cw);
        ClassReader cr = new ClassReader(A.class.getCanonicalName());
        cr.accept(cv, ClassReader.SKIP_FRAMES);

        byte[] resultBytecode = cw.toByteArray();

        ClassReader crCount = new ClassReader(resultBytecode);
        InvokeMethodCounter yieldCounter = new InvokeMethodCounter("yield");
        crCount.accept(yieldCounter, ClassReader.SKIP_FRAMES);

        Assert.assertEquals(9, yieldCounter.getCount());
    }

}
