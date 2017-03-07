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

import org.objectweb.asm.Opcodes;

public class Utils {
    public static final int ASM_VERSION = Opcodes.ASM5;

    private static volatile int consumedCPU = (int) System.currentTimeMillis();

    public static void consumeCPU(int tokens) {
        int t = consumedCPU; // volatile read
        for (int i = tokens; i > 0; i--)
            t += (t * 0x5DEECE66DL + 0xBL + i) & (0xFFFFFFFFFFFFL);
        if (t == 42)
            consumedCPU += t;
        // TODO commented code, remove it
//        System.out.println("Tokens: " + tokens);
//        System.out.println("ConsumeCPU: " + consumedCPU);
    }
}
