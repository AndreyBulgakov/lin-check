package me.aevd.lintesting.util;

public interface Caller {
    public void reload();
    Result call(int method, Object... args);
    Actor[][] generateActors(int numThreads);
}
