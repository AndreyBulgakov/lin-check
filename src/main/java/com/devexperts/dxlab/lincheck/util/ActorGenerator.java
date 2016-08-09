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
import java.lang.reflect.Parameter;
import java.util.*;

public class ActorGenerator {
    private int methodId;
    private Method method;
    private Map<Parameter, Params> rangeArgs;
    private boolean actorIsMutable = true;


    public ActorGenerator(int methodId, Method method, Map<Parameter, Params> rangeArgs) {
        this.methodId = methodId;
        this.method = method;
        this.rangeArgs = rangeArgs;
    }

    public void setMutable(boolean actorIsMutable) {
        this.actorIsMutable = actorIsMutable;
    }

    public boolean isMutable() {
        return actorIsMutable;
    }

    public Actor generate(int indActor) {
        MethodParameter[] args = new MethodParameter[rangeArgs.size()];
        Parameter[] parameters = method.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            args[i] = new MethodParameter(parameters[i].getType().getTypeName(), MyRandom.fromParams(rangeArgs.get(parameters[i])));
        }

        Actor act = new Actor(indActor, method, isMutable(), args);
        act.methodName = method.getName();
        return act;
    }

    @Override
    public String toString() {
        return "ActorGenerator{" +
                "methodId=" + methodId +
                ", name='" + method.getName() + '\'' +
                ", rangeArgs=" + rangeArgs.values() +
                '}';
    }
}
