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

import com.devexperts.dxlab.lincheck.generators.ParameterGenerator;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

public class ActorGenerator implements Cloneable {
    private int methodId;
    private Method method;
    private ParameterGenerator[] rangeArgs;
    private boolean actorIsMutable = true;


    public ActorGenerator(int methodId, Method method, ParameterGenerator[] rangeArgs) {
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
        Object[] args = new Object[rangeArgs.length];
        ParameterGenerator[] parameters = rangeArgs;
        for (int i = 0; i < parameters.length; i++) {
            args[i] = parameters[i].generate();
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
                ", rangeArgs=" + rangeArgs.toString() +
                '}';
    }

    @Override
    public ActorGenerator clone() {
        ActorGenerator clone = new ActorGenerator(methodId, method, rangeArgs);
        clone.setMutable(isMutable());
        return clone;
    }
}
