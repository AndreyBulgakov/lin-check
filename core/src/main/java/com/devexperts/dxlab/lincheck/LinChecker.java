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

import co.paralleluniverse.fibers.instrument.JavaAgent;
import com.ea.agentloader.AgentLoader;

/**
 * Class to start Linearization checking
 */
public class LinChecker {
/*
/**
     * LinChecker run method. Use LinChecker.check(TestClass.class) in junit test class
     * @param testClass class that contains CTest
     * @throws AssertionError if find Non-linearizable executions
     *//*

    public static void check(Class testClass) throws AssertionError {
            new LinChecker0(testClass).check();
    }
*/

    /**
     * TODO do not pass instance, remove this method
     * LinChecker run method. Use LinChecker.check(this) in junit test class
     * @param testInstance object that contains CTest
     * @throws AssertionError if find Non-linearizable executions
     */
    public static void check(Object testInstance) throws AssertionError {
//            if (testInstance.getClass().isAnnotationPresent(GreenTest.class))
//                AgentLoader.loadAgentClass(JavaAgent.class.getName(), "d");
        AgentLoader.loadAgentClass(JavaAgent.class.getName(), "");
        new LinChecker0(testInstance.getClass()).check();
    }

}