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

package com.devexperts.dxlab.lincheck;

import com.devexperts.dxlab.lincheck.annotations.ReadOnly;

import java.lang.reflect.Method;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

/**
 * The actor entity describe the operation with it's parameter
 * which is executed in the test. To test the linearizability
 * the actors permutations are considered.
 */
class Actor {
    private final Method method;
    private final boolean readOnly;
    private final List<Object> arguments;
    private final List<Class<? extends Throwable>> handledExceptions;

    Actor(Method method, List<Object> arguments, List<Class<? extends Throwable>> handledExceptions) {
        this.method = method;
        this.readOnly = method.isAnnotationPresent(ReadOnly.class);
        this.arguments = arguments;
        this.handledExceptions = handledExceptions;
    }

    @Override
    public String toString() {
        return method.getName() + "(" + arguments.stream().map(Object::toString).collect(Collectors.joining(", ")) + ")"
            + (readOnly ? "[r]" : "[w]");
    }

    public Method getMethod() {
        return method;
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    public List<Object> getArguments() {
        return arguments;
    }

    public List<Class<? extends Throwable>> getHandledExceptions() {
        return handledExceptions;
    }

    public boolean handlesExceptions() {
        return !handledExceptions.isEmpty();
    }
}