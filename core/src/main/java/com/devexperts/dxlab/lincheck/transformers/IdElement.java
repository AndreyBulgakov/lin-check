package com.devexperts.dxlab.lincheck.transformers;

/**
 * Id class for mapping
 */
public class IdElement {

    private ClassLoader classLoader;
    private String declaringClass;
    private String methodName;
    private int    lineNumber;

    public IdElement(ClassLoader classLoader, String declaringClass, String methodName, int lineNumber) {
        this.classLoader = classLoader;
        this.declaringClass = declaringClass;
        this.methodName = methodName;
        this.lineNumber = lineNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IdElement idElement = (IdElement) o;

        if (lineNumber != idElement.lineNumber) return false;
        if (!classLoader.equals(idElement.classLoader)) return false;
        if (!declaringClass.equals(idElement.declaringClass)) return false;
        return methodName.equals(idElement.methodName);
    }

    @Override
    public int hashCode() {
        int result = classLoader.hashCode();
        result = 31 * result + declaringClass.hashCode();
        result = 31 * result + methodName.hashCode();
        result = 31 * result + lineNumber;
        return result;
    }
}
