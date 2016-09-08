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

package com.devexperts.dxlab.lincheck.util;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Actor {
    public int ind;
    public Method method;
    public Object[] args;
    public String methodName;
    public boolean isMutable;


    public Actor(int ind, Method method, boolean isMutable) {
        this.ind = ind;
        this.method = method;
        this.isMutable = isMutable;
    }

    public Actor(int ind, Method method, boolean isMutable, Object... args) {
        this.ind = ind;
        this.method = method;
        this.isMutable = isMutable;
        this.args = args;
    }

    private static String argsToString(Object[] args) {
        if (args == null)
            return "null";

        int iMax = args.length - 1;
        if (iMax == -1)
            return "";

        StringBuilder b = new StringBuilder();
        for (int i = 0; ; i++) {
            b.append(String.valueOf(args[i]));
            if (i == iMax)
                return b.toString();
            b.append(", ");
        }
    }

    @Override
    public String toString() {
        return ind +
                "_" + methodName +
                "(" + Arrays.toString(args) +
                ")";
    }
}
