package me.aevd.lintesting.util;

public class Result {
    ResultType resType;
    Integer value;

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
    public void setException() {
        resType = ResultType.EXCEPTION;
    }
    public void setTimeout() {
        resType = ResultType.TIMEOUT;
    }

    @Override
    public String toString() {
        return "Result{" + resType + " : " + value + "}";
//        return "Result{" +
//                "resType=" + resType +
//                ", value=" + value +
//                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Result result = (Result) o;

        if (resType != result.resType) return false;
        if (value != null ? !value.equals(result.value) : result.value != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = resType.hashCode();
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
    }
}

enum ResultType {
    VALUE, VOID, EXCEPTION, TIMEOUT, UNDEFINED
}
