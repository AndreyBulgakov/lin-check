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

public class ActorGenerator implements Cloneable {
    private int methodId;
    private Method method;
    private Params[] rangeArgs;
    private boolean actorIsMutable = true;
    private int numberOfValidStreams = 0;


    public ActorGenerator(int methodId, Method method, int numberOfValidStreams, Params[] rangeArgs) {
        this.methodId = methodId;
        this.method = method;
        this.numberOfValidStreams = numberOfValidStreams;
        this.rangeArgs = rangeArgs;
    }

    public void setMutable(boolean actorIsMutable) {
        this.actorIsMutable = actorIsMutable;
    }

    public void decNumberOfValidStreams(){
        if (numberOfValidStreams != -1)
            numberOfValidStreams--;
    }
    public int getNumberOfValidStreams(){
        return numberOfValidStreams;
    }
    public void setNumberOfValidStreams(int numberOfValidStreams){
        this.numberOfValidStreams = numberOfValidStreams;
    }

    public boolean isMutable() {
        return actorIsMutable;
    }

    public Actor generate(int indActor) {
        MethodParameter[] args = new MethodParameter[rangeArgs.length];
        Params[] parameters = rangeArgs;
        for (int i = 0; i < parameters.length; i++) {
            args[i] = new MethodParameter(parameters[i].type, MyRandom.fromParams(parameters[i]));
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
    public ActorGenerator clone(){
        return new ActorGenerator(methodId, method, numberOfValidStreams, rangeArgs);
    }
}
