package main.java.com.devexperts.dxlab.lincheck.util;

public class Result {
    ResultType resType;
    Integer value;
    Class exceptionClass;


    public Result() {
        resType = ResultType.UNDEFINED;
    }

    public void setValue(Integer value) {
        resType = ResultType.VALUE;
        this.value = value;
    }

    public void setVoid() {
        resType = ResultType.VOID;
    }
    public void setException(Exception e) {
        resType = ResultType.EXCEPTION;
        exceptionClass = e.getClass();
    }
    public void setTimeout() {
        resType = ResultType.TIMEOUT;
    }

    @Override
    public String toString() {
        if (resType == ResultType.EXCEPTION) {
            return "R{" + resType + " : " + exceptionClass.getSimpleName() + "}";
        }
        return "R{" + resType + " : " + value + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Result result = (Result) o;

        if (resType != result.resType) return false;
        if (exceptionClass != null ? !exceptionClass.equals(result.exceptionClass) : result.exceptionClass != null) {
            return false;
        }
        if (value != null ? !value.equals(result.value) : result.value != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = resType.hashCode();
        result = 31 * result + (value != null ? value.hashCode() : 0);
        result = 31 * result + (exceptionClass != null ? exceptionClass.hashCode() : 0);
        return result;
    }
}

enum ResultType {
    VALUE, VOID, EXCEPTION, TIMEOUT, UNDEFINED
}
