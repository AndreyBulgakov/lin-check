package com.devexperts.dxlab.lincheck;

import com.devexperts.dxlab.lincheck.annotations.CTest;
import com.devexperts.dxlab.lincheck.annotations.HandleExceptionAsResult;
import com.devexperts.dxlab.lincheck.annotations.Operation;
import com.devexperts.dxlab.lincheck.annotations.Param;
import com.devexperts.dxlab.lincheck.annotations.Reset;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Contains information about operations (see {@link Operation}) and reset method (see {@link Reset}).
 * Several {@link CTest tests} can refer to one structure
 * (i.e. one test class could have several {@link CTest} annotations)
 */
class CTestStructure {
    private final List<ActorGenerator> actorGenerators;
    private final Method resetMethod;

    private CTestStructure(List<ActorGenerator> actorGenerators, Method resetMethod) {
        this.actorGenerators = actorGenerators;
        this.resetMethod = resetMethod;
    }

    static CTestStructure getFromTestClass(Class<?> testClass) {
        // Read named parameter generators (declared for class)
        Map<String, ParameterGenerator<?>> namedPGs = new HashMap<>();
        for (Param paramAnn : testClass.getAnnotationsByType(Param.class)) {
            if (paramAnn.name().isEmpty()) {
                throw new IllegalArgumentException("@Param name in class declaration cannot be empty");
            }
            namedPGs.put(paramAnn.name(), createParameterGenerator(paramAnn));
        }
        // Create actor generators
        List<ActorGenerator> actorGenerators = new ArrayList<>();
        Method resetMethod = null;
        for (Method m : testClass.getDeclaredMethods()) {
            if (m.isAnnotationPresent(Reset.class)) {
                if (resetMethod != null) {
                    throw new IllegalArgumentException("Only one @Reset method can be presented");
                }
                resetMethod = m;
            }
            if (m.isAnnotationPresent(Operation.class)) {
                Operation operationAnn = m.getAnnotation(Operation.class);
                List<ParameterGenerator<?>> pgs;
                if (operationAnn.params().length > 0) { // Use named parameter generators
                    if (operationAnn.params().length != m.getParameterCount()) {
                        throw new IllegalArgumentException("Invalid count of parameter " +
                            "generators for " + m.toString() + " method");
                    }
                    pgs = Arrays.stream(operationAnn.params())
                        .map(namedPGs::get)
                        .collect(Collectors.toList());
                } else {
                    pgs = Arrays.stream(m.getParameters())
                        .map(p -> p.getAnnotation(Param.class))
                        .map(paramAnn -> paramAnn.name().isEmpty() ? createParameterGenerator(paramAnn) : namedPGs.get(paramAnn.name()))
                        .collect(Collectors.toList());
                }
                HandleExceptionAsResult handleExceptionAsResultAnn = m.getAnnotation(HandleExceptionAsResult.class);
                List<Class<? extends Throwable>> handledExceptions = handleExceptionAsResultAnn != null ?
                    Arrays.asList(handleExceptionAsResultAnn.value()) : Collections.emptyList();
                actorGenerators.add(new ActorGenerator(m, pgs, handledExceptions));
            }
        }
        // Create CTest class configuration
        return new CTestStructure(actorGenerators, resetMethod);
    }

    private static ParameterGenerator<?> createParameterGenerator(Param paramAnn) {
        try {
            return paramAnn.generator().getConstructor(String.class)
                .newInstance(paramAnn.generatorConfiguration());
        } catch (Exception e) {
            throw new IllegalStateException("Cannot create parameter generator", e);
        }
    }

    List<ActorGenerator> getActorGenerators() {
        return actorGenerators;
    }

    Method getResetMethod() {
        return resetMethod;
    }
}
