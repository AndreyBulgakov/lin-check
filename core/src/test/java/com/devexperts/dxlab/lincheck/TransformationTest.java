package com.devexperts.dxlab.lincheck;

import com.devexperts.dxlab.lincheck.strategy.ConsumeCPUStrategy;
import com.devexperts.dxlab.lincheck.strategy.StrategyHolder;
import com.devexperts.dxlab.lincheck.transformers.BeforeSharedVariableClassVisitor;
import com.devexperts.dxlab.lincheck.utils.InvokeMethodCounter;
import com.devexperts.dxlab.transformigclasses.A;
import org.junit.Assert;
import org.junit.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

import java.io.IOException;


public class TransformationTest {

    // TODO rename it, you do not test ConsumeCPU here
    @Test
    public void TestConsumeCPU() throws IOException {
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        StrategyHolder.setCurrentStrategy(new ConsumeCPUStrategy(100));
        ClassVisitor cv = new BeforeSharedVariableClassVisitor(cw, this.getClass().getClassLoader());
        ClassReader cr = new ClassReader(A.class.getCanonicalName());
        cr.accept(cv, ClassReader.EXPAND_FRAMES);

        byte[] resultBytecode = cw.toByteArray();

        ClassReader crCount = new ClassReader(resultBytecode);
        InvokeMethodCounter CPUcounter = new InvokeMethodCounter("onSharedVariableAccess");
        crCount.accept(CPUcounter, ClassReader.SKIP_FRAMES);

        Assert.assertEquals(9, CPUcounter.getCount());
    }

}
