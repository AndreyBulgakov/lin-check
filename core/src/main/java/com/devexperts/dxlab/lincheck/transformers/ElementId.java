package com.devexperts.dxlab.lincheck.transformers;

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

/**
 * Id class for mapping // TODO for which mapping?
 */
public class ElementId {
    private final String declaringClass;
    private final String methodName;
    private final String methodDesc;
    private final int instructionNumber;

    ElementId(String declaringClass, String methodName, String methodDesc, int lineNumber) {
        this.declaringClass = declaringClass;
        this.methodName = methodName;
        this.methodDesc = methodDesc;
        this.instructionNumber = lineNumber;
    }

    public String getMethodName(){
        return methodName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ElementId elementId = (ElementId) o;

        if (instructionNumber != elementId.instructionNumber) return false;
        if (!declaringClass.equals(elementId.declaringClass)) return false;
        if (!methodName.equals(elementId.methodName)) return false;
        return methodDesc.equals(elementId.methodDesc);
    }

    @Override
    public int hashCode() {
        int result = declaringClass.hashCode();
        result = 31 * result + methodName.hashCode();
        result = 31 * result + methodDesc.hashCode();
        result = 31 * result + instructionNumber;
        return result;
    }

    @Override
    public String toString() {
        return "ElementId{" +
                "declaringClass='" + declaringClass + '\'' +
                ", methodName='" + methodName + '\'' +
                ", methodDesc='" + methodDesc + '\'' +
                ", instructionNumber=" + instructionNumber +
                '}';
    }
}
