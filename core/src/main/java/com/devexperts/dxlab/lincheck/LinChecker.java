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

import sun.misc.URLClassPath;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * Class to start Linearization checking
 */
public class LinChecker {
    /**
     * TODO do not pass instance, remove this method
     * LinChecker run method. Use LinChecker.check(this) in junit test class
     * @param testInstance object that contains CTest
     * @throws AssertionError if find Non-linearizable executions
     */
    //=v -Dco.paralleluniverse.fibers.verifyInstrumentation=true
    public static void check(Object testInstance) throws AssertionError {
        LinChecker0.check(testInstance);
//        try {
//            // Get current URLs from parrent classLoader
//            Field ucp = URLClassLoader.class.getDeclaredField("ucp");
//            ucp.setAccessible(true);
//            URL[] classLoaderUrls = ((URLClassPath) ucp.get(LinChecker.class.getClassLoader())).getURLs();
//            // Loading instruments
//            QuasarLoader urlClassLoader = new QuasarLoader(classLoaderUrls);
//            Thread.currentThread().setContextClassLoader(urlClassLoader);
//            // Log
////          helper.setLog(true, true);
//            Class<?> instrumentedLincheckClass = urlClassLoader.loadClass("com.devexperts.dxlab.lincheck.LinChecker0");
//            Class<?> instrumentedTestInstance = urlClassLoader.loadClass(testInstance.getClass().getName());
//            Object newInstance = instrumentedTestInstance.newInstance();
//            Method m = instrumentedLincheckClass.getMethod("check", Object.class);
//            m.invoke(null, newInstance);
//        } catch (NoSuchMethodException | InstantiationException | InvocationTargetException | NoSuchFieldException | ClassNotFoundException | IllegalAccessException e) {
//            e.printStackTrace();
//        }

    }



}