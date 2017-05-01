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

import co.paralleluniverse.fibers.instrument.QuasarURLClassLoader;

import java.net.URL;
import java.util.HashMap;

/**
 * Created by andrey on 4/4/17.
 */
public class QuasarLoader extends QuasarURLClassLoader {
    private HashMap<String, Class> cache = new HashMap<>();

    public QuasarLoader(URL[] urls) {
        super(urls);
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        // Load transformed class from cache if it exists

        // Print loading class
//        System.out.println("Loading QuasarLoader: " + name);
        Class result = cache.get(name);
        if (result != null) {
            return result;
        }
        // Secure some packages
        if (shouldIgnoreClass(name)) {
            // Print delegated class
//            System.out.println("Loaded by App:" + name);
            return super.loadClass(name);
        }
        //        System.out.println("Loaded by quasar: " + name);
        result = super.findClass(name);
        cache.put(name, result);
        return result;
    }

    /***
     * Check if class should be ignored for transforming and defining
     * @param className checking class name
     * @return result of checking class
     */
    private static boolean shouldIgnoreClass(String className) {
        return
                className == null ||
//                        className.startsWith("com.devexperts.dxlab.lincheck.") &&
//                                !className.startsWith("com.devexperts.dxlab.lincheck.tests.") ||
                        className.startsWith("sun.") ||
                        className.startsWith("javax.") ||
                        className.startsWith("java."); //||
//                        className.startsWith("co.paralleluniverse.");
        // TODO let's transform java.util.concurrent
    }

}
