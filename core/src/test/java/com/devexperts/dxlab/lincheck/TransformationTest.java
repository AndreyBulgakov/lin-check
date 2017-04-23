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

    @Test
    public void TestOnSharedVariableInsertion() throws IOException {
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        StrategyHolder.setCurrentStrategy(new ConsumeCPUStrategy(100));
        ClassVisitor cv = new BeforeSharedVariableClassVisitor(cw);
        ClassReader cr = new ClassReader(A.class.getCanonicalName());
        cr.accept(cv, ClassReader.EXPAND_FRAMES);

        byte[] resultBytecode = cw.toByteArray();

        ClassReader crCount = new ClassReader(resultBytecode);
        InvokeMethodCounter CPUcounter = new InvokeMethodCounter("onSharedVariableAccess");
        crCount.accept(CPUcounter, ClassReader.SKIP_FRAMES);

        Assert.assertEquals(9, CPUcounter.getCount());
    }

}
