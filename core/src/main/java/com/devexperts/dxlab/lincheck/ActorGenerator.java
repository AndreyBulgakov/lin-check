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

import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The instances of this class are used to generate {@link Actor actors}
 * using {@link ParameterGenerator parameter generators}.
 */
class ActorGenerator {
    private final Method method;
    private final List<ParameterGenerator<?>> parameterGenerators;
    private final List<Class<? extends Throwable>> handledExceptions;

    ActorGenerator(Method method, List<ParameterGenerator<?>> parameterGenerators,
        List<Class<? extends Throwable>> handledExceptions)
    {
        this.method = method;
        this.parameterGenerators = parameterGenerators;
        this.handledExceptions = handledExceptions;
    }

    Actor generate() {
        return new Actor(method, parameterGenerators.stream()
            .map(ParameterGenerator::generate).collect(Collectors.toList()), handledExceptions);
    }
}