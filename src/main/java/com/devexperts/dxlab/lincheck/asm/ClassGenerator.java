/*
 *  Lincheck - Linearizability checker
 *  Copyright (C) 2015 Devexperts LLC
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.devexperts.dxlab.lincheck.asm;


import java.lang.reflect.Constructor;

import com.devexperts.dxlab.lincheck.asm.templ.*;
import com.devexperts.dxlab.lincheck.util.Interval;
import com.devexperts.dxlab.lincheck.util.MethodParameter;
import jdk.internal.org.objectweb.asm.Opcodes;

public class ClassGenerator implements Opcodes {

    public static Generated generate(
            Object test,
            String pointedClassName,
            String generatedClassName, // "com/devexperts/dxlab/lincheck/asmtest/Generated2"
            String testFieldName, // queue
            String testClassName, // com/devexperts/dxlab/lincheck/tests/custom/QueueTestAnn
            String[] methodNames,
            MethodParameter[][] parameters,
            String[] methodTypes
    ) throws Exception {
        DynamicClassLoader loader = new DynamicClassLoader();

        Class<?> clz = null;
        clz = loader.define(pointedClassName,
                GeneratedDump.dump(
                        generatedClassName,
                        testFieldName,
                        testClassName,
                        methodNames,
                        parameters,
                        methodTypes
                ));

        Constructor<?>[] ctors = clz.getConstructors();
        Constructor<?> ctor = ctors[1];
        Generated o = (Generated) ctor.newInstance(test);
        return o;
    }

    private static class DynamicClassLoader extends ClassLoader {
        public Class<?> define(String className, byte[] bytecode) {
            return super.defineClass(className, bytecode, 0, bytecode.length);
        }
    };
}
