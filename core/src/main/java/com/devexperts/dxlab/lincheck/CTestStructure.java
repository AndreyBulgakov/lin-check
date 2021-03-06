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

import com.devexperts.dxlab.lincheck.annotations.HandleExceptionAsResult;
import com.devexperts.dxlab.lincheck.annotations.OpGroupConfig;
import com.devexperts.dxlab.lincheck.annotations.Operation;
import com.devexperts.dxlab.lincheck.annotations.Param;
import com.devexperts.dxlab.lincheck.annotations.Reset;
import com.devexperts.dxlab.lincheck.stress.StressCTest;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Contains information about operations (see {@link Operation}) and reset method (see {@link Reset}).
 * Several {@link StressCTest tests} can refer to one structure
 * (i.e. one test class could have several {@link StressCTest} annotations)
 */
public class CTestStructure {
    public final List<ActorGenerator> actorGenerators;
    public final List<OperationGroup> operationGroups;
    public final Method resetMethod;

    private CTestStructure(List<ActorGenerator> actorGenerators, List<OperationGroup> operationGroups, Method resetMethod) {
        this.actorGenerators = actorGenerators;
        this.operationGroups = operationGroups;
        this.resetMethod = resetMethod;
    }

    public static CTestStructure getFromTestClass(Class<?> testClass) {
        // Read named parameter paramgen (declared for class)
        Map<String, ParameterGenerator<?>> namedGens = new HashMap<>();
        for (Param paramAnn : testClass.getAnnotationsByType(Param.class)) {
            if (paramAnn.name().isEmpty()) {
                throw new IllegalArgumentException("@Param name in class declaration cannot be empty");
            }
            namedGens.put(paramAnn.name(), createGenerator(paramAnn));
        }
        // Read group configurations
        Map<String, OperationGroup> groupConfigs = new HashMap<>();
        for (OpGroupConfig opGroupConfigAnn: testClass.getAnnotationsByType(OpGroupConfig.class)) {
            groupConfigs.put(opGroupConfigAnn.name(), new OperationGroup(opGroupConfigAnn.name(),
                opGroupConfigAnn.nonParallel()));
        }
        // Create actor paramgen
        List<ActorGenerator> actorGenerators = new ArrayList<>();
        Method resetMethod = null;
        for (Method m : testClass.getDeclaredMethods()) {
            // Reset
            if (m.isAnnotationPresent(Reset.class)) {
                if (resetMethod != null)
                    throw new IllegalArgumentException("Only one @Reset method can be presented");
                resetMethod = m;
            }
            // Operation
            if (m.isAnnotationPresent(Operation.class)) {
                Operation operationAnn = m.getAnnotation(Operation.class);
                // Check that params() in @Operation is empty or has the same size as the method
                if (operationAnn.params().length > 0 && operationAnn.params().length != m.getParameterCount()) {
                    throw new IllegalArgumentException("Invalid count of paramgen for " + m.toString()
                        + " method in @Operation");
                }
                // Construct list of parameter paramgen
                final List<ParameterGenerator<?>> gens = new ArrayList<>();
                for (int i = 0; i < m.getParameterCount(); i++) {
                    String nameInOperation = operationAnn.params().length > 0 ? operationAnn.params()[i] : null;
                    gens.add(getOrCreateGenerator(m, m.getParameters()[i], nameInOperation, namedGens));
                }
                // Get list of handled exceptions if they are presented
                HandleExceptionAsResult handleExceptionAsResultAnn = m.getAnnotation(HandleExceptionAsResult.class);
                List<Class<? extends Throwable>> handledExceptions = handleExceptionAsResultAnn != null ?
                    Arrays.asList(handleExceptionAsResultAnn.value()) : Collections.emptyList();
                ActorGenerator actorGenerator = new ActorGenerator(m, gens, handledExceptions, operationAnn.runOnce());
                actorGenerators.add(actorGenerator);
                // Get list of groups and add this operation to specified ones
                String opGroup = operationAnn.group();
                if (!opGroup.isEmpty()) {
                    OperationGroup operationGroup = groupConfigs.get(opGroup);
                    if (operationGroup == null)
                        throw new IllegalStateException("Operation group " + opGroup + " is not configured");
                    operationGroup.actors.add(actorGenerator);
                }
            }
        }
        // Create StressCTest class configuration
        return new CTestStructure(actorGenerators, new ArrayList<>(groupConfigs.values()), resetMethod);
    }

    private static ParameterGenerator<?> getOrCreateGenerator(Method m, Parameter p, String nameInOperation,
        Map<String, ParameterGenerator<?>> namedGens)
    {
        // Read @Param annotation on the parameter
        Param paramAnn = p.getAnnotation(Param.class);
        // If this annotation not presented use named generator based on name presented in @Operation or parameter name.
        if (paramAnn == null) {
            // If name in @Operation is presented, return the generator with this name,
            // otherwise return generator with parameter's name
            String name = nameInOperation != null ? nameInOperation :
                (p.isNamePresent() ? p.getName() : null);
            if (name == null) {
                throw new IllegalStateException("Generator for parameter \'" + p.getName() + "\" in method \""
                    + m.getName() + "\" is missed. Try to specify it manually or enable parameter name feature in your " +
                    "compiler (use \"-parameters\" javac option for this purpose).");
            }
            return checkAndGetNamedGenerator(namedGens, name);
        }
        // If the @Param annotation is presented check it's correctness firstly
        if (!paramAnn.name().isEmpty() && !(paramAnn.gen() == ParameterGenerator.Dummy.class))
            throw new IllegalStateException("@Param should have either name or gen with optionally configuration");
        // If @Param annotation specifies generator's name then return the specified generator
        if (!paramAnn.name().isEmpty())
            return checkAndGetNamedGenerator(namedGens, paramAnn.name());
        // Otherwise create new parameter generator
        return createGenerator(paramAnn);
    }

    private static ParameterGenerator<?> createGenerator(Param paramAnn) {
        try {
            return paramAnn.gen().getConstructor(String.class).newInstance(paramAnn.conf());
        } catch (Exception e) {
            throw new IllegalStateException("Cannot create parameter gen", e);
        }
    }

    private static ParameterGenerator<?> checkAndGetNamedGenerator(Map<String, ParameterGenerator<?>> namedGens, String name) {
        return Objects.requireNonNull(namedGens.get(name), "Unknown generator name: \"" + name + "\"");
    }

    public static class OperationGroup {
        public final String name;
        public final boolean nonParallel;
        public final List<ActorGenerator> actors;

        public OperationGroup(String name, boolean nonParallel) {
            this.name = name;
            this.nonParallel = nonParallel;
            this.actors = new ArrayList<>();
        }

        @Override
        public String toString() {
            return "OperationGroup{" +
                "name='" + name + '\'' +
                ", nonParallel=" + nonParallel +
                ", actors=" + actors +
                '}';
        }
    }
}
