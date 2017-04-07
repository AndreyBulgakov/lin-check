package com.devexperts.dxlab.lincheck;

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
//            System.out.println("Loaded by super:" + name);
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
                        className.startsWith("java.") ||
                        className.startsWith("co.paralleluniverse.");
        // TODO let's transform java.util.concurrent
    }

}
