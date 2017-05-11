package com.devexperts.dxlab.lincheck;

/**
 * Created by andrey on 5/9/17.
 */
public class CleanClassLoader extends ClassLoader {
    public CleanClassLoader(ClassLoader parent) {
        super(parent);
    }

    public CleanClassLoader() {
    }

    Class<? extends TestThreadExecution> defineTestThreadExecution(String className, byte[] bytecode) {
        return (Class<? extends TestThreadExecution>) super.defineClass(className, bytecode, 0, bytecode.length);
    }

}
